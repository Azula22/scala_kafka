import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

class PersistanceConsumer {

  val system = ActorSystem()
  implicit val exec: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)

  val TOPIC = "all"
  private val BROKER_LIST = "kafka:9090"

  val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings[String, String](system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(BROKER_LIST)
    .withPollTimeout(3.hours)
    .withWakeupTimeout(3.hours)
    .withCommitTimeout(3.hours)

  val done: Future[Done] = Consumer.plainSource(consumerSettings, Subscriptions.topics(TOPIC))
    .map { msg => Future
        .fromTry(SignUpRow.apply(msg.value()))
        .flatMap(r => DB.create(r))}
    .runWith(Sink.ignore)

}
