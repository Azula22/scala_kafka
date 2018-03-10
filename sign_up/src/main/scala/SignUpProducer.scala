import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import models.SignUpServer
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.Future


class SignUpProducer(system: ActorSystem) {

  private val BROKER_LIST = "kafka:9092"
  private val TOPIC = "all"

  private val producerSetting = ProducerSettings[String, SignUpServer](system, None, None)
    .withBootstrapServers(BROKER_LIST)

  def send(su: SignUpServer): Future[Done] = Source
    .single(su)
    .map(su => new ProducerRecord[String, SignUpServer](BROKER_LIST, su))
    .runWith(Producer.plainSink[String, SignUpServer](producerSetting))

}
