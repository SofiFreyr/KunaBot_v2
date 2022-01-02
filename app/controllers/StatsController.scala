package controllers

import javax.inject._
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import play.api.mvc._
import play.api.libs.json.{JsArray, Json}

import scala.concurrent.duration._
import scala.concurrent.{Await, CanAwait, ExecutionContext, Future, Promise}
import services.MarketStats

import scala.language.postfixOps


@Singleton
class StatsController @Inject()(cc: ControllerComponents, sys: ActorSystem)(implicit executor: ExecutionContext) extends AbstractController(cc) {

  def message = Action.async {
    getStats(0.second).map { msg => Ok(views.html.stats(msg)) }
  }

  def getStats(delayTime: FiniteDuration): Future[JsArray] = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val timeout = Timeout(5 second).duration

    val promise: Promise[JsArray] = Promise[JsArray]()
    system
      .scheduler
      .scheduleAtFixedRate(delayTime,Timeout(1 second).duration)(() =>
        promise.success((Json parse (Await.result(Unmarshal(Await.result(MarketStats.get(), timeout).entity).to[String], timeout))).as[JsArray])
      ) (system.dispatcher)// run scheduled tasks using the actor system's dispatcher
    promise.future
  }

}
