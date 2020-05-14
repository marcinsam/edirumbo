package pl.marboz.scala.edirumbo.domain.reservation

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import ch.qos.logback.classic.{Logger, LoggerContext}
import javax.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory
import pl.marboz.scala.edirumbo.domain.movie.Movie
import pl.marboz.scala.edirumbo.domain.seat.RoomRowSeat
import pl.marboz.scala.edirumbo.infrastructure.{MovieRepository, ReservationRepository, RoomRowSeatRepository}
import pl.marboz.scala.edirumbo.utils.OperationResult
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/** 0
 *
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 28.04.2020 08:31
 */
@Singleton
class ReservationService @Inject()(reservationRepository: ReservationRepository,
                                   movieRepository: MovieRepository,
                                   dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[reservation] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def bookInTransaction(reservationFromRequestList: List[ReservationInput]): Future[OperationResult] = {
    if (reservationFromRequestList.isEmpty)
      return Future.successful(OperationResult.Incorrect("Empty reservation list"))
    val movieId = reservationFromRequestList(0).movieRoomId
    val seats: List[Int] = reservationFromRequestList.map(res => res.roomRowSeatId)
    val operationResult: Future[OperationResult] = reservationRepository.lock(movieId, seats) flatMap {
      case cnt: Int => if (cnt == reservationFromRequestList.size) {
        val action = (for {
          movie <- movieRepository.findById(movieId)
          reservationToBeBookedList <- reservationRepository.findByMovieRoomIdAndRoomRowSeatIdList(movieId, seats)
          operationResult <- processReservationList(reservationToBeBookedList, reservationFromRequestList, movie)
        } yield (operationResult)).transactionally

        db.run(action) flatMap {
          case updateCnt: Int => if (updateCnt == reservationFromRequestList.size) {
            Future.successful(OperationResult.OK("Reservation successful"))
          } else {
            Future.successful(OperationResult.Incorrect("reservation not completed"))
          }
        }
      } else {
        reservationRepository.unlock(movieId, seats)
        Future.successful(OperationResult.Incorrect("Already booked"))
      }
    }
    operationResult
  }

  def bookInTransaction(reservationFromRequest: ReservationInput): Future[OperationResult] = {
    val operationResult: Future[OperationResult] = reservationRepository.lock(reservationFromRequest.movieRoomId,
      reservationFromRequest.roomRowSeatId) flatMap {
      case cnt: Int => if (cnt == 1) {
        val action = (for {
          movie <- movieRepository.findById(reservationFromRequest.movieRoomId)
          reservationToBeBooked <- reservationRepository.findByMovieRoomIdAndRoomRowSeatId(reservationFromRequest.movieRoomId, reservationFromRequest.roomRowSeatId)
          operationResult <- processReservation(reservationToBeBooked, reservationFromRequest, movie)
        } yield (operationResult)).transactionally

        db.run(action) flatMap {
          case updateCnt: Int => if (updateCnt == 1) {
            Future.successful(OperationResult.OK("Reservation succefull"))
          } else {
            Future.successful(OperationResult.Incorrect("reservation not completed"))
          }
        }
      } else {
        Future.successful(OperationResult.Incorrect("Already booked"))
      }
    }
    operationResult
  }

  private def processReservation(reservationFromDb: Reservation, reservationFromRequest: ReservationInput, movie: Movie): DBIO[Int] = {
    val expirationTime = movie.screeningTime.minus(30, ChronoUnit.MINUTES)
    reservationRepository.update(new Reservation(reservationFromRequest.movieRoomId,
      reservationFromRequest.roomRowSeatId, Option(reservationFromRequest.name),
      Option(reservationFromRequest.surname), Option(reservationFromRequest.ticketPriceId),
      ReservationStatus.RESERVED.toString, Option(expirationTime), reservationFromDb.created, LocalDateTime.now))
  }

  private def processReservationList(reservationFromDbList: Seq[Reservation], reservationFromRequest: List[ReservationInput], movie: Movie): DBIO[Int] = {
    val reservationMap = reservationFromDbList.map(el => el.roomRowSeatId -> el).toMap
    val expirationTime = movie.screeningTime.minus(30, ChronoUnit.MINUTES)

    val batchUpdateData = reservationFromRequest.map(el => {
      new Reservation(el.movieRoomId, el.roomRowSeatId, Option(el.name), Option(el.surname),
        Option(el.ticketPriceId), ReservationStatus.RESERVED.toString, Option(expirationTime),
        reservationMap.get(el.roomRowSeatId).get.created, LocalDateTime.now)
    })

    reservationRepository.batchUpdate(batchUpdateData).map(res => res.sum)
  }

}
