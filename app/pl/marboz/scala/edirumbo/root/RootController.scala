package pl.marboz.scala.edirumbo.root

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

/**
  * Created by Marcin Bozek on 2019-07-22.
  */
@Singleton
class RootController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello")
  }

}
