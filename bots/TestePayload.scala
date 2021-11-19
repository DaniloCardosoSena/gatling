package Bots

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class TestePayload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .inferHtmlResources(BlackList(""".*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .acceptHeader("text/css,*/*;q=0.1")
    .acceptEncodingHeader("gzip, deflate")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Content-Type" -> "application/json"
  )

  val csvFeeder = csv("data/massa2.csv").circular

  val scn = scenario("TestePayload")

    .repeat(10) {

      feed(csvFeeder)
        .exec(http("1.0 - POST - jsonplaceholder API RawBody")
          .post("/todos")
          .body(RawFileBody("data/payload.txt"))
          .headers(headers_0)
          .check(status.is(201)))
          .pause(1) // pausa de 1 segundo

        .exec(http("2.0 - POST - jsonplaceholder API ELBody")
          .post("/todos")
          .body(ElFileBody("data/payload2.txt"))
          .headers(headers_0)
          .check(status.is(201)))
    //    .check(regex("""title":\s"testeb",""").is("title\": \"testeb\",")))
          .pause(1) // pausa de 1 segundo

    }
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}