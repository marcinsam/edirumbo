package pl.marboz.scala.edirumbo.domain.reservation

import java.time.LocalDateTime

import play.api.libs.json.Json

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 23.04.2020 18:36
 */
case class Reservation(movieRoomId:Long, roomRowSeatId:Int, var name:Option[String],
                       var surname:Option[String], var ticketPriceId:Option[Int], status:String,
                       expirationTime:Option[LocalDateTime], created:LocalDateTime, updated:LocalDateTime
)

object Reservation {
  implicit val movieFormat = Json.format[Reservation]

}
