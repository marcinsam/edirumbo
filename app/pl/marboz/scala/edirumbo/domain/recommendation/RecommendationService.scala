package pl.marboz.scala.edirumbo.domain.recommendation

import javax.inject.Inject
import pl.marboz.scala.edirumbo.domain.reservation.{Reservation, ReservationStatus}
import pl.marboz.scala.edirumbo.domain.seat.RoomRowSeat
import pl.marboz.scala.edirumbo.infrastructure.{MovieRoomRepository, ReservationRepository, RoomRowSeatRepository}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 12.05.2020 21:15
 */
class RecommendationService @Inject()(reservationRepository: ReservationRepository,
                                      roomRowSeatRepository: RoomRowSeatRepository,
                                      movieRoomRepository: MovieRoomRepository,
                                      dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[recommendation] val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def recommend(movieRoomId: Int):Future[String] = {
    val action = (for {
      allReservations <- reservationRepository.findByMovieRoomId(movieRoomId)
      movieRoom <- movieRoomRepository.findById(movieRoomId)
      seats <- roomRowSeatRepository.findByRoom(movieRoom.roomId)
      operationResult <- recommend(seats, allReservations)
    } yield (operationResult)).transactionally

    db.run(action) flatMap {
      res => Future.successful(res)
    }
  }

  def markReserved(reservation: Reservation, matrix: Array[Array[String]], seatsMap:Map[Int, RoomRowSeat]) = {
    if(reservation.status != ReservationStatus.NEW.toString) {
      matrix(seatsMap.get(reservation.roomRowSeatId).get.row - 1)(seatsMap.get(reservation.roomRowSeatId).get.seat - 1) = "x"
    }
  }

  def recommend(seats:Seq[RoomRowSeat], reservations:Seq[Reservation]):DBIO[String] = {
    val r = seats.last.row
    val s = seats.last.seat
    val seatsMap = seats.map(el => el.id -> el).toMap
    val matrix = Array.ofDim[String](r, s)
    matrix.indices.foreach(i => manhattanMetric(matrix(i), i, r))
    reservations.map(el => markReserved(el, matrix, seatsMap))
    DBIO.successful(matrix.map(_.mkString).mkString("\n") + "\n")
  }

  def manhattanMetric(row: Array[String], cnt: Int, numberOfRows: Int) = {
    val length = row.length
    val halfLength = row.length / 2
    for (i <- 0 until length) {
      row(i) = (((halfLength - i)).abs + ((numberOfRows - cnt - 1) * 2)).toString
    }
  }

}
