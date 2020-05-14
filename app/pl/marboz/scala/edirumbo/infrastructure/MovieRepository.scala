package pl.marboz.scala.edirumbo.infrastructure

import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import pl.marboz.scala.edirumbo.domain.movie.Movie
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Marcin Bozek on 2019-08-05.
 */
@Singleton
class MovieRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[infrastructure] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val movies = TableQuery[MoviesTable]

  def create(title: String, screeningTime: LocalDateTime): Future[Movie] = db.run {
    (movies.map(m => (m.title, m.screeningDateTime))
      returning movies.map(_.id)
      into ((titleScreeningTimeRoom, id) => Movie(id, titleScreeningTimeRoom._1, titleScreeningTimeRoom._2))
      ) += (title, screeningTime)
  }

  def list(): Future[Seq[Movie]] = db.run {
    movies.result
  }

  def find(maybeAfter: Option[LocalDateTime], maybeBefore: Option[LocalDateTime]): Future[Seq[Movie]] = db.run {
//    TODO: return distinct title, screeningDateTime
    movies.filterOpt(maybeAfter) { case (table, after) =>
      table.screeningDateTime > after
    }
      .filterOpt(maybeBefore) { case (table, before) =>
        table.screeningDateTime < before
      }.sortBy(table => table.screeningDateTime).sortBy(table => table.title)
      .result
  }

  def findById(id: Long):DBIO[Movie] = {
    movies.filter{table => table.id === id}.result.head
  }

  private class MoviesTable(tag: Tag) extends Table[Movie](tag, "movie") {

    def * = (id, title, screeningDateTime) <> ((Movie.apply _).tupled, Movie.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def screeningDateTime = column[LocalDateTime]("screening_datetime")

//    def room = column[Int]("room")

  }

}
