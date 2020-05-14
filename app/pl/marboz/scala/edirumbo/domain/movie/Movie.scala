package pl.marboz.scala.edirumbo.domain.movie

import java.time.LocalDateTime

import cats.data.Reader
import pl.marboz.scala.edirumbo.infrastructure.MovieRepository
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Marcin Bozek on 2019-08-06.
 */
case class Movie(id: Long, title: String, screeningTime: LocalDateTime)

object Movie {
  implicit val movieFormat = Json.format[Movie]

  def findBeetwenScreeningTime(maybeAfter: Option[LocalDateTime], maybeBefore: Option[LocalDateTime]) = Reader[MovieRepository, Future[Seq[Movie]]] {repo =>
    repo.find(maybeAfter, maybeBefore)
  }

  def create(title: String, screeningTime: LocalDateTime) = Reader[MovieRepository, Future[Movie]] {
    repo => repo.create(title, screeningTime)
  }

  def list() = Reader[MovieRepository, Future[Seq[Movie]]] {
    repo => repo.list()
  }
}
