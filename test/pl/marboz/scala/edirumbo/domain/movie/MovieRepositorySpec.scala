package pl.marboz.scala.edirumbo.domain.movie

import java.time.LocalDateTime

import pl.marboz.scala.edirumbo.infrastructure.MovieRepository
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{PlaySpecification, WithApplication}

import scala.concurrent.{Await, ExecutionContext}

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 01.05.2020 14:06
 */
class MovieRepositorySpec extends PlaySpecification {

  "MovieRepository" should {
    val appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder()
    val dbConfProvider = appBuilder.injector.instanceOf[DatabaseConfigProvider]
    val movieRepository:MovieRepository = new MovieRepository(dbConfProvider)(ExecutionContext.global)
    val fakeApplication = appBuilder.build()

    "list all movies" in new WithApplication(fakeApplication) {
      //given:
      val elem0 = Movie(1, "Ostatnia paróweczka hrabiega", LocalDateTime.parse("2020-01-10T11:35"))
      val elem8 = Movie(9, "Wielbłąd", LocalDateTime.parse("2020-01-16T18:05"))
      //when:
      val result = await(movieRepository.list)
      //then:
      result.size mustEqual 9
      result(0) mustEqual elem0
      result(8) mustEqual elem8
    }
  }

}