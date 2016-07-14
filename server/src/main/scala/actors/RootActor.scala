package actors

import akka.actor.SupervisorStrategy.Restart
import akka.remote.ContainerFormats.ActorRef
import akka.actor._
import models.{NewsLinkModel, NewsLinkStore}
import services.WebServiceParser
import spa.shared.TabId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by molmsted on 5/23/2016.
  */
object RootActor {
  def props(serviceParser: WebServiceParser, linksModel: NewsLinkStore) = Props(new RootActor(serviceParser: WebServiceParser, linksModel: NewsLinkStore))
}

class RootActor(serviceParser: WebServiceParser, linksModel: NewsLinkStore) extends Actor {
  //implicit val node = Cluster(ScraperCluster.system1)

  //ScraperCluster.system1.eventStream.subscribe(self, classOf[DbUpdateComplete])
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case e: Exception => println("Exception detected by root")
      Restart
  }
  //initialize DB actors
  println("try to creat actor")

  val feedParseActors = serviceParser.siteMapping.keys.map(key =>
    context.system.actorOf(Props(classOf[SupervisorActor], key, serviceParser, linksModel))
  )

  println("root actor init")

  def receive = {
    case Startup => println("Started")
    case _ => println("test")
  }
}

case object Startup
