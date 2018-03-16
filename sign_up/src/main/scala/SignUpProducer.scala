import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.QueueOfferResult.Enqueued
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.{ExecutionContextExecutor, Future}


class SignUpProducer(implicit val mat: ActorMaterializer) {

  implicit val system: ActorSystem = mat.system
  implicit val exec: ExecutionContextExecutor = system.dispatcher

  private val BROKER_LIST = "localhost:9092"
  private val TOPIC = "all"

  private val producerSetting = ProducerSettings[Array[Byte], String](system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers(BROKER_LIST)

  val producer: KafkaProducer[Array[Byte], String] = producerSetting.createKafkaProducer()

  val (queue, _) = Source
      .queue[String](0, OverflowStrategy.backpressure)
      .map(su => new ProducerRecord[Array[Byte], String](TOPIC, su))
      .toMat(Producer.plainSink[Array[Byte], String](producerSetting, producer))(Keep.both)
      .run()

  def publish(su: String) = {
    queue.offer(su).flatMap{
      case Enqueued => Future.successful{}
      case otherResult => Future.failed(new Throwable(s"Unexpected queue result ${otherResult.toString}"))
    }
  }

}
