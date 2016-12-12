import play.api.libs.json.Writes

/**
  * Created by crimson on 12/8/16.
  */
abstract class Parser[A] {
  val seq: Seq[A]
  val tjs: Writes[A]

  protected def parseInt(x: String): Int = x match {
    case "" => 0
    case _ => x.toInt
  }

  protected def parseDouble(x: String): Double = x match {
    case "" => 0.0
    case _ => x.toDouble
  }
}
