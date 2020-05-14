package pl.marboz.scala.edirumbo.api

import java.time.LocalDateTime

import javax.inject.Inject
import org.slf4j.LoggerFactory
import pl.marboz.scala.edirumbo.domain.movie.Movie
import pl.marboz.scala.edirumbo.infrastructure.MovieRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Marcin Bozek on 2019-08-05.
 */
class MovieController @Inject()(movieRepository: MovieRepository,
                                cc: MessagesControllerComponents
                               )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val logger = LoggerFactory.getLogger(this.getClass)
  val movieForm: Form[MovieFormInput] = Form {
    mapping(
      "title" -> nonEmptyText,
      "screeningDateTime" -> localDateTime
    )(MovieFormInput.apply)(MovieFormInput.unapply)
  }

  def add = Action { implicit request =>
    movieForm.bindFromRequest.fold(
      errorForm => {
        Ok("Incorrect")
      },
      movie => {
        Movie.create(movie.title, movie.screeningDateTime).map { _ =>
          Ok("Ok")
        } run (movieRepository)
      }
    )
  }

  def getMovies = Action.async { implicit request =>
    movieRepository.list().map { movie =>
      Ok(Json.toJson(movie))
    }

  }

  def getMovies(after: Option[LocalDateTime], before: Option[LocalDateTime]) = Action.async { implicit request =>
    logger.info(s"Received request for movies from $after to $before")
    Movie.findBeetwenScreeningTime(after, before)
      .map { movieFuture => fromFutureToSeq(movieFuture) }
      .run(movieRepository)
  }

  def fromFutureToSeq(future:Future[Seq[Movie]]):Future[Result] = {
    future.map { movieList => Ok(Json.toJson(movieList)) }
  }

}

case class MovieFormInput(title: String, screeningDateTime: LocalDateTime)