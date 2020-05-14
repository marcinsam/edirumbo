package pl.marboz.scala.edirumbo.domain.reservation

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import pl.marboz.scala.edirumbo.infrastructure.{MovieRepository, ReservationRepository}
import pl.marboz.scala.edirumbo.utils.OperationResult
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{PlaySpecification, WithApplication}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 04.05.2020 22:49
 */
@RunWith(classOf[JUnitRunner])
class ReservationServiceSpec extends PlaySpecification {

  "ReservationService" should {
    val appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder()
    val dbConfProvider:DatabaseConfigProvider = appBuilder.injector.instanceOf[DatabaseConfigProvider]
    val movieRepository:MovieRepository = new MovieRepository(dbConfProvider)(ExecutionContext.global)
    val reservationRepository:ReservationRepository = new ReservationRepository(dbConfProvider)(ExecutionContext.global)
    val reservationService: ReservationService =
      new ReservationService(reservationRepository, movieRepository, dbConfProvider)(ExecutionContext.global)
    val fakeApplication = appBuilder.build()
    val dbConfig = dbConfProvider.get[JdbcProfile]
    import dbConfig._

    "2 reservations at once" in new WithApplication(fakeApplication) {
      val result1 = reservationService.bookInTransaction(List(ReservationInput(1, 10, "Marcel", "Choinka", 1)))
      val result2 = reservationService.bookInTransaction(List(ReservationInput(1, 10, "Pirelli", "Pieczonka", 3)))
      val operationResult1 = await {result1}
      val operationResult2 = await {result2}
      val resultReservation:Reservation = await { db.run {reservationRepository.findByMovieRoomIdAndRoomRowSeatId(1, 10)} }
      resultReservation.status mustEqual ReservationStatus.RESERVED.toString
      operationResult1.description mustEqual "Reservation successful"
      resultReservation.name must beSome("Marcel")
      resultReservation.surname must beSome("Choinka")
      resultReservation.ticketPriceId must beSome(1)
//      operationResult2.description mustEqual "Reservation succefull"
//      resultReservation.name must beSome("Pirelli")
//      resultReservation.surname must beSome("Pieczonka")
//      resultReservation.ticketPriceId must beSome(3)
    }
  }
}
