package pl.marboz.scala.edirumbo.domain.reservation

import play.api.libs.json.{Json, Reads}

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 10.05.2020 12:33
 */
case class ReservationInput (movieRoomId: Int, roomRowSeatId: Int, name: String, surname: String, ticketPriceId: Int)

object ReservationInput {
  implicit val reads: Reads[ReservationInput] = Json.reads[ReservationInput]
}
