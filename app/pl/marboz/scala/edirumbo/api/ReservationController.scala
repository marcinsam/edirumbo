package pl.marboz.scala.edirumbo.api

import javax.inject.Inject
import pl.marboz.scala.edirumbo.domain.reservation.{ReservationInput, ReservationService, ReservationStatus}
import pl.marboz.scala.edirumbo.infrastructure.ReservationRepository
import pl.marboz.scala.edirumbo.utils.OperationResult
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Marcin Bozek on 2019-09-03.
 */
//TODO: Maturity level
class ReservationController @Inject()(reservationService: ReservationService,
                                      reservationRepository: ReservationRepository,
                                      cc: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val reservationForm: Form[ReservationInput] = Form {
    mapping(
      "movieId" -> number,
      "roomRowSeatId" -> number,
      "name" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "ticketPriceId" -> number
    )(ReservationInput.apply)(ReservationInput.unapply)
  }

  def getFreeReservations(movieRoomId: Long) = Action.async { implicit request =>
    reservationRepository.findByStatusAndMovieRoomId(ReservationStatus.NEW.toString, movieRoomId).map { reservation =>
      Ok(Json.toJson(reservation))
    }
  }

  def getAllReservations(movieRoomId: Long) = Action.async { implicit request =>
    reservationRepository.findFutureByMovieRoomId(movieRoomId).map { reservation =>
      Ok(Json.toJson(reservation))
    }
  }

  def book = Action.async { implicit request =>
    reservationForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok("Incorrect"))
      },
      reservationFromRequest => {
        reservationService.bookInTransaction(reservationFromRequest) flatMap  {
          case OperationResult.OK(description) => Future.successful(Ok(description))
          case OperationResult.Incorrect(description) => Future.successful(BadRequest(description))
        }
      }
    )
  }

//  @BodyParser.Of(BodyParser.Json)
  def bookGroup = Action.async(parse.json) { implicit request =>
    request.body.validate[List[ReservationInput]].fold(
      errorForm => {
        Future.successful(Ok("Incorrect"))
      },
      reservationFromRequest => {
        reservationService.bookInTransaction(reservationFromRequest) flatMap  {
          case OperationResult.OK(description) => Future.successful(Ok(description))
          case OperationResult.Incorrect(description) => Future.successful(BadRequest(description))
        }
      }
    )
  }
}
