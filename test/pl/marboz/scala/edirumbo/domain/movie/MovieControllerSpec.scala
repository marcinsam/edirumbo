package pl.marboz.scala.edirumbo.domain.movie

import java.time.LocalDateTime

import org.junit.runner._
import org.specs2.mock.Mockito
import org.specs2.runner._
import pl.marboz.scala.edirumbo.infrastructure.MovieRepository
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 28.04.2020 22:13
 */
@RunWith(classOf[JUnitRunner])
class MovieControllerSpec extends PlaySpecification with Mockito {

  "MovieController" should {

    val movieRepositoryMock = mock[MovieRepository]
    val application = new GuiceApplicationBuilder()
      .overrides(bind[MovieRepository].toInstance(movieRepositoryMock))
      .build()

    "list all movies" in new WithApplication {
      movieRepositoryMock.find(Option.empty, Option.empty)returns Future.successful(ListBuffer(Movie(1, "Example movie", LocalDateTime.parse("2020-01-20T07:15:00"))))
      val res = route(application, FakeRequest(GET, "/movies")).get

      status(res) must equalTo(OK)
      contentAsString(res) must equalTo("[{\"id\":1,\"title\":\"Example movie\",\"screeningTime\":\"2020-01-20T07:15:00\"}]")
    }
  }

}
