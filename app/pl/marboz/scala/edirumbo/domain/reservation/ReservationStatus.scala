package pl.marboz.scala.edirumbo.domain.reservation

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 26.04.2020 16:19
 */
object ReservationStatus extends Enumeration {
  type ReservationStatus = Value
  val NEW, FREE, RESERVED, INVALID, PAID, IN_PROGRESS = Value
}
