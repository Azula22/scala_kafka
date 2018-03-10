import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import models.{SignUpClient, SignUpServer}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.{Failure, Success}

object HTTPServer extends JsonSupport {

  implicit val system: ActorSystem = ActorSystem()
  implicit val exec: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  implicit val timeout: FiniteDuration = 10.seconds
  val kafka = new SignUpProducer(system)

  val signUp = (put & pathEndOrSingleSlash & entity(as[SignUpClient])){ entity =>
    val signUpServer = SignUpServer(entity)
    val action = kafka.send(SignUpServer(entity))
    onComplete(action){
      case Success(_) => complete(signUpServer.id.toString)
      case Failure(_) => complete("KO")
    }
  }

  def routes: Route = signUp

  def start(host: String, port: Int) = Http().bindAndHandle(routes, host, port = port)

}
