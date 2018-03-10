import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContextExecutor, Future}


class SignUpProducer(implicit val mat: ActorMaterializer) {

  implicit val system: ActorSystem = mat.system
  implicit val exec: ExecutionContextExecutor = system.dispatcher

  private val BROKER_LIST = "localhost:9090"
  private val TOPIC = "all"

  private val producerSetting = ProducerSettings[String, String](system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(BROKER_LIST)

  val producer: KafkaProducer[String, String] = producerSetting.createKafkaProducer()

  val (queue, _) = Source
      .queue[String](10, OverflowStrategy.backpressure)
      .map(su => new ProducerRecord[String, String](TOPIC, su))
      .toMat(Producer.plainSink[String, String](producerSetting, producer))(Keep.both)
      .run()

  def publish(su: String): Future[Done] = {
    queue.offer(su).flatMap(_ => queue.watchCompletion())
  }

}
