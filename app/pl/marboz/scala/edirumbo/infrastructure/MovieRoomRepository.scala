package pl.marboz.scala.edirumbo.infrastructure

import javax.inject.{Inject, Singleton}
import pl.marboz.scala.edirumbo.domain.movieroom.MovieRoom
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 12.05.2020 21:04
 */
@Singleton
class MovieRoomRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[infrastructure] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val movieRooms = TableQuery[MovieRoomTable]

  def findById(id: Int): DBIO[MovieRoom] =
    movieRooms.filter {table => table.id === id}.result.head

  private class MovieRoomTable(tag: Tag) extends Table[MovieRoom](tag, "movie_room") {

    def * = (id, movieId, roomId) <> ((MovieRoom.apply _).tupled, MovieRoom.unapply)

    def id = column[Int]("id", O.PrimaryKey)
    def movieId = column[Int]("movie_id", O.PrimaryKey)
    def roomId = column[Short]("room_id")
  }

}
