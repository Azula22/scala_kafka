import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object HTTPServer {

  implicit val system: ActorSystem = ActorSystem()
  implicit val exec: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  implicit val timeout: FiniteDuration = 10.seconds

  def routes: Route = ??? //TODO implement

  def start(host: String, port: Int) = Http().bindAndHandle(routes, host, port = port)

}
