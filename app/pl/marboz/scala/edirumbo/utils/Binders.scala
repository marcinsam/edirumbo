package pl.marboz.scala.edirumbo.utils

import java.time.LocalDateTime

import play.api.mvc.QueryStringBindable

/**
 * Created by Marcin Bozek on 2019-09-03.'
 */
object Binders {

  implicit object localDateTimeBinder extends QueryStringBindable.Parsing[LocalDateTime] (
    LocalDateTime.parse, _.toString, (k: String, e: Exception) => "Cannot parse %s as LocalDateTime: %s".format(k, e.getMessage)
  )
}
