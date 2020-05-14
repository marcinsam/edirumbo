package pl.marboz.scala.edirumbo.infrastructure

import javax.inject.{Inject, Singleton}
import pl.marboz.scala.edirumbo.domain.seat.RoomRowSeat
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

/**
 * Created by Marcin Bozek on 2019-09-14.
 */
@Singleton
class RoomRowSeatRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[infrastructure] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val seats = TableQuery[RoomRowSeatTable]

  def findByRoom(room: Int): DBIO[Seq[RoomRowSeat]] =
    //    TODO: return distinct title, screeningDateTime
    seats.filter {table => table.room === room}.result

  private class RoomRowSeatTable(tag: Tag) extends Table[RoomRowSeat](tag, "room_row_seat") {

    def * = (id, room, row, seat) <> ((RoomRowSeat.apply _).tupled, RoomRowSeat.unapply)

    def id = column[Int]("id", O.PrimaryKey)
    def room = column[Int]("room")
    def row = column[Int]("row")
    def seat = column[Int]("seat")
  }

}
