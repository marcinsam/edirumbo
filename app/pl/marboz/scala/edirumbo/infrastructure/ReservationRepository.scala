package pl.marboz.scala.edirumbo.infrastructure

import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import pl.marboz.scala.edirumbo.domain.reservation.{Reservation, ReservationStatus}
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Marcin Bozek on 2019-09-03.
 */
@Singleton
class ReservationRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[infrastructure] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val reservations = TableQuery[ReservationsTable]

  def findFutureByMovieRoomId(movieRoomId: Long): Future[Seq[Reservation]] = db.run {
    //    TODO: return distinct title, screeningDateTime
    findByMovieRoomId(movieRoomId)
  }

  def findByMovieRoomId(movieRoomId: Long): DBIO[Seq[Reservation]] =
    //    TODO: return distinct title, screeningDateTime
    reservations.filter {table => table.movieRoomId === movieRoomId}
      .result

  def findByStatusAndMovieRoomId(status:String, movieRoomId:Long): Future[Seq[Reservation]] = db.run {
    //    TODO: return distinct title, screeningDateTime
    reservations.filter {table => table.status === status && table.movieRoomId === movieRoomId}
      .result
  }

  def findByMovieRoomIdAndRoomRowSeatId(movieRoomId:Long, roomRowSeatId:Int): DBIO[Reservation] = {
    reservations.filter {table => table.movieRoomId === movieRoomId && table.roomRowSeatId === roomRowSeatId}
      .result.head
  }

  def findByMovieRoomIdAndRoomRowSeatIdList(movieRoomId:Long, seats:List[Int]): DBIO[Seq[Reservation]] = {
    reservations.filter {table => table.movieRoomId === movieRoomId && table.roomRowSeatId.inSet(seats)}
      .result
  }

  def update(reservation: Reservation):DBIO[Int] =
    reservations.filter(table => table.movieRoomId === reservation.movieRoomId && table.roomRowSeatId === reservation.roomRowSeatId
      && table.status===ReservationStatus.IN_PROGRESS.toString )
      .map(r => r).update(reservation)

  def lock(movieRoomId:Long, roomRowSeatId:Int) =
    db.run {reservations.filter(table => table.movieRoomId === movieRoomId && table.roomRowSeatId === roomRowSeatId
      && table.status===ReservationStatus.NEW.toString )
      .map(r => r.status).update(ReservationStatus.IN_PROGRESS.toString) }

  def lock(movieRoomId:Long, seats:List[Int]) =
    db.run {reservations.filter(table => table.movieRoomId === movieRoomId && table.roomRowSeatId.inSet(seats)
      && table.status===ReservationStatus.NEW.toString )
      .map(r => r.status).update(ReservationStatus.IN_PROGRESS.toString) }

  def unlock(movieRoomId:Long, seats:List[Int]) =
    db.run {reservations.filter(table => table.movieRoomId === movieRoomId && table.roomRowSeatId.inSet(seats)
      && table.status===ReservationStatus.IN_PROGRESS.toString )
      .map(r => r.status).update(ReservationStatus.NEW.toString) }

  def batchUpdate(reservations:List[Reservation]) = {
    val toBeInserted = reservations.map { row => this.reservations.insertOrUpdate(row) }
    DBIO.sequence(toBeInserted)
  }

  private class ReservationsTable(tag: Tag) extends Table[Reservation](tag, "reservation") {

    def * = (movieRoomId, roomRowSeatId, name, surname, ticketPriceId,
  status, expirationTime, created, updated) <> ((Reservation.apply _).tupled, Reservation.unapply)

    def movieRoomId = column[Long]("movie_room_id", O.PrimaryKey)
    def roomRowSeatId = column[Int]("room_row_seat_id", O.PrimaryKey)
    def name = column[Option[String]]("name")
    def surname = column[Option[String]]("surname")
    def ticketPriceId = column[Option[Int]]("ticket_price_id")
    def status = column[String]("status")
    def expirationTime = column[Option[LocalDateTime]] ("expiration_time")
    def created = column[LocalDateTime]("created")
    def updated = column[LocalDateTime]("updated")
  }
}
