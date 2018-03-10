import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.SignUpClient
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val signUpFormat = jsonFormat2(SignUpClient)

}
