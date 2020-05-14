package pl.marboz.scala.edirumbo.utils

/**
 * @author <a href="mailto:marboz85@gmail.com">Marcin Bozek</a>
 *         Date: 28.04.2020 08:34
 */
sealed trait OperationResult { def description:String }

object OperationResult {
  case class OK (description:String) extends OperationResult
  case class Incorrect (description:String) extends OperationResult
}