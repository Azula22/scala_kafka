import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import models.{SignUpClient, SignUpServer}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.{Failure, Success, Try}

class HTTPServer(kafka: SignUpProducer, implicit val mat: ActorMaterializer) {

  private implicit val system: ActorSystem = mat.system
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val timeout: FiniteDuration = 10.seconds
  private implicit val formats: DefaultFormats.type = DefaultFormats

  val signUp: Route = (put & pathEndOrSingleSlash & entity(as[String])){ entity =>
    Try(parse(entity).extract[SignUpClient])
      .map { data =>
        val signUpServer = SignUpServer(data)
        val action = kafka.publish(write(signUpServer))
        onComplete(action){
          case Success(_) => complete(signUpServer.id.toString)
          case Failure(_) => complete("KO")
        }
      }.recover { case err =>
          err.printStackTrace()
          complete("KO")
      }.get
  }

  def start(host: String, port: Int)
           (implicit mat: ActorMaterializer, system: ActorSystem) =
    Http().bindAndHandle(signUp, host, port = port)
      .map     { _ => println("Server started") }
      .recover { case err => println(s"Can't start server due to ${err.printStackTrace()}")}

}
