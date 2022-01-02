package services

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Awaitable, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object MarketStats {
  implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.executionContext

  def get(): Awaitable[HttpResponse] = {
    //scala.io.Source.fromURL("https://api.kuna.io/v3/tickers?symbols=btcuah").mkString
    val getRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = "https://api.kuna.io/v3/tickers?symbols=ALL"
    )

    val responseFuture: Future[HttpResponse] = Http(system = system).singleRequest(getRequest)

    return responseFuture
  }
}
