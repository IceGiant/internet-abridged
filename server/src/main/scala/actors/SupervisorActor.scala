package actors

import akka.actor.SupervisorStrategy.Restart
import akka.remote.ContainerFormats.ActorRef
import akka.actor._
import models.NewsLinkStore
import services.{TitleLink, WebServiceParser}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by molmsted on 5/23/2016.
  */
object SupervisorActor {
  def props(tabId: String, serviceParser: WebServiceParser, linksModel: NewsLinkStore) = Props(new SupervisorActor(tabId, serviceParser: WebServiceParser, linksModel: NewsLinkStore))
}

class SupervisorActor(tabId: String, serviceParser: WebServiceParser, linksModel: NewsLinkStore) extends Actor{
  //implicit val node = Cluster(ScraperCluster.system1)

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case e: Exception => println("Exception detected by supervisor")
      Restart
  }
  //create/use scraping actors on a timer
  val scraper = context.system.actorOf(Props(classOf[ScraperActor], tabId, serviceParser, linksModel))

  def deleteAndRefresh() = {
    //println(s"started scraping $tabId")
    scraper ! StartScraping
  }

  context.system.scheduler.schedule(0.seconds, 15.minutes)(deleteAndRefresh())

  def receive = {
    case _ => println("Unexpected msg to DbActor")//bad message
  }
}
