import java.util.UUID

import akka.http.scaladsl.model
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

class SignInTest extends FlatSpecLike with Matchers with MockFactory with ScalatestRouteTest {

  class ProducerWithMaterializer extends SignUpProducer()

  val producer: SignUpProducer = mock[ProducerWithMaterializer]

  val email = "bla"
  val password = "blabla"
  val producerRequest = s"""{"email": "$email", "password": "$password"}"""
  val dummyRequest = "hello"

  (producer.publish _).expects(*).onCall { d: String =>
    val row = SignUpRow.apply(d)
    println(row)
    DB.create(row.get).map(_ => {})
  }

  val server = new HTTPServer(producer, materializer)

  "HTTP server" should "take correct data, return id and write it to db" in {

    Put("/", HttpEntity(model.ContentType(MediaTypes.`application/json`), producerRequest)) ~> server.signUp ~> check {
      response.status shouldBe StatusCodes.OK
      val responseBody = Try(UUID.fromString(responseAs[String]))
      responseBody.isSuccess shouldBe true
      val (dbId, dbEmail, dbPass) = Await.result(DB.get(responseBody.get), Duration.Inf)
      dbId shouldBe responseBody.get.toString
      dbEmail shouldBe email
      dbPass shouldBe password
    }

  }


  it should "return KO response on dummy request" in {

    Put("/", HttpEntity(model.ContentType(MediaTypes.`application/json`), dummyRequest)) ~> server.signUp ~> check{
      response.status shouldBe StatusCodes.OK
      responseAs[String] shouldBe "KO"
    }

  }

}