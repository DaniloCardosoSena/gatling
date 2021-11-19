package Bots

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class RegexTest extends Simulation {

  val varDePorto = "Boston"
  val varParaPorto = "London"

  val httpProtocol = http
    .baseUrl("https://blazedemo.com")
    .inferHtmlResources(BlackList(""".*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")


  val headers_0 = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7",
    "origin" -> "https://blazedemo.com",
    "sec-ch-ua" -> """Chromium";v="94", "Google Chrome";v="94", ";Not A Brand";v="99""")


  val scn = scenario("BlazeDemoGatling")

    .repeat(3) {

      exec(http("1.0 - GET - Home Blazedemo")
        .get("/")
        .headers(headers_0)

        .resources(http("1.1 - GET - /assets/bootstrap.min.js")
          .get("/assets/bootstrap.min.js")
          .headers(headers_0),

          http("1.2 - GET - /assets/bootstrap-table.js")
            .get("/assets/bootstrap-table.js")
            .headers(headers_0),

          http("1.3 - GET - /img/glyphicons-halflings.png")
            .get("/img/glyphicons-halflings.png")
            .headers(headers_0)))

        .pause(1)

        .exec(http("2.0 - POST - /reserve.php")
          .post("/reserve.php")
          .check(regex("""value="(.*?)"\sname="flight"""").exists.saveAs("varRegex"))
          .headers(headers_0)
          .formParam("fromPort", s"${varDePorto}")
          .formParam("toPort", s"${varParaPorto}")
          .check(status.is(200))

          .resources(http("2.1 - GET - /assets/bootstrap.min.js")
            .get("/assets/bootstrap.min.js")
            .headers(headers_0),

            http("2.2 - GET - /assets/bootstrap-table.js")
              .get("/assets/bootstrap-table.js")
              .headers(headers_0)))

        .pause(1)

        .exec(http("3.0 - POST - /purchase.php")
          .post("/purchase.php")
          .headers(headers_0)
          .formParam("flight", "${varRegex}")
          .formParam("price", "472.56")
          .formParam("airline", "Virgin America")
          .formParam("fromPort", "Boston")
          .formParam("toPort", "London")

          .resources(http("3.1 - GET - /assets/bootstrap.min.js")
            .get("/assets/bootstrap.min.js")
            .headers(headers_0),

            http("3.2 - GET - /assets/bootstrap-table.js")
              .get("/assets/bootstrap-table.js")
              .headers(headers_0),

            http("3.3 - GET - /assets/bootstrap.min.css")
              .get("/assets/bootstrap.min.css")
              .headers(headers_0),

            http("3.4 - GET - /assets/bootstrap-table.css")
              .get("/assets/bootstrap-table.css")
              .headers(headers_0),

            http("3.5 - GET - /img/glyphicons-halflings.png")
              .get("/img/glyphicons-halflings.png")
              .headers(headers_0)))

        .pause(1)

        .exec(http("4.0 - POST - /confirmation.php")
          .post("/confirmation.php")
          .headers(headers_0)
          .formParam("_token", "")
          .formParam("inputName", "TESTE")
          .formParam("address", "123 Main ST")
          .formParam("city", "SP")
          .formParam("state", "SP")
          .formParam("zipCode", "123456")
          .formParam("cardType", "visa")
          .formParam("creditCardNumber", "0001")
          .formParam("creditCardMonth", "11")
          .formParam("creditCardYear", "2017")
          .formParam("nameOnCard", "TESTE")

          .resources(http("4.1 - GET - /img/glyphicons-halflings.png")
            .get("/img/glyphicons-halflings.png")
            .headers(headers_0)))
    }

      setUp(scn
        .inject(
          /*     rampConcurrentUsers(0) to(3) during(30 seconds),
      constantConcurrentUsers(3) during(30 seconds)
    )
  )	.maxDuration(60 seconds)*/
          atOnceUsers(1)))

        .protocols(httpProtocol)
    }
