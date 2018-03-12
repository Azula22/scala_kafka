import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object Main {

  def main(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem()
    implicit val exec: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)
    val kafka = new SignUpProducer()
    new HTTPServer(kafka, materializer).start("0.0.0.0", 8080)
  }

}
