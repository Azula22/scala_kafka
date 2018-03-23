import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

case class SignUpRow(id: String, email: String, password: String)

object SignUpRow {
  implicit val _ = DefaultFormats
  def apply(s: String): Try[SignUpRow] = Try {
    parse(s).extract[SignUpRow]
  }
}
