package Bots

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class TesteMassa extends Simulation {

  val httpProtocol = http
    .baseUrl("https://computer-database.gatling.io")
    .inferHtmlResources(BlackList(""".*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .acceptHeader("text/css,*/*;q=0.1")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Upgrade-Insecure-Requests" -> "1",
    "sec-ch-ua" -> """Google Chrome";v="95", "Chromium";v="95", ";Not A Brand";v="99""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows")

  val csvFeeder = csv("data/massa.csv").circular


  val scn = scenario("TesteMassa")

    .repeat(5) {

      feed(csvFeeder)

      .exec(http("1.0 - GET - Home Computer Gatling")
        .get("/computers")
        .headers(headers_0)

        .resources(http("1.1 - GET - /assets/css/main.css")
          .get("/assets/css/main.css")
          .headers(headers_0),

          http("1.2 - GET - /assets/css/bootstrap.min.css")
            .get("/assets/css/bootstrap.min.css")
            .headers(headers_0)))

        .pause(1) // pausa de 1 segundo

        .exec(http("2.0 - GET - /computers/new")
          .get("/computers/new")
          .headers(headers_0))

        .pause(1)

        .exec(http("3.0 - POST - /computers")
          .post("/computers")
          .headers(headers_0)
          .formParam("name", "${name}")
          .formParam("introduced", "${intro}")
          .formParam("discontinued", "${discon}")
          .formParam("company", "${comp}")
          .check(regex("""has\sbeen\screated""").is("has been created")))    // Assercao Response msg created
          //.check(regex("""Computer\sComputerPC1\shas\sbeen\screated""").is("Computer ComputerPC1 has been created")))    // Assercao Response msg

    }
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}