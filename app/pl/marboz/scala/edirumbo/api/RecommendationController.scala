package pl.marboz.scala.edirumbo.api

import javax.inject.Inject
import pl.marboz.scala.edirumbo.domain.recommendation.RecommendationService
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.ExecutionContext

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 12.05.2020 21:33
 */
class RecommendationController @Inject() (recommendationService: RecommendationService,
                                          cc: MessagesControllerComponents)
                                         (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def recommend(movieRoomId: Int) = Action.async { implicit request =>
    recommendationService.recommend(movieRoomId) map {
      result => Ok(result)
    }
  }

}
