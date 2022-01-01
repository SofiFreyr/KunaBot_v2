package controllers

import javax.inject._
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{Await, CanAwait, ExecutionContext, Future, Promise}
import services.MarketStats

import scala.language.postfixOps


@Singleton
class StatsController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getStats = {
    implicit val system = ActorSystem()
    implicit val executor = system.dispatcher
    implicit val materializer = ActorMaterializer()
    Action { Ok(Unmarshal(Await.result(MarketStats.get(), Timeout(5 second).duration).entity).to[String].value.get.get)}
  }

}
