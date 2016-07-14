package actors

import akka.actor.SupervisorStrategy.Restart
import akka.remote.ContainerFormats.ActorRef
import akka.actor._
import models.{NewsLinkModel, NewsLinkStore}
import services.{TitleLink, WebServiceParser}
import spa.shared.LinkObject

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by molmsted on 5/23/2016.
  */
object ScraperActor {
  def props(tabId: String, serviceParser: WebServiceParser, linksModel: NewsLinkStore) = Props(new ScraperActor(tabId, serviceParser: WebServiceParser, linksModel: NewsLinkStore))
}

class ScraperActor(tabId: String, serviceParser: WebServiceParser, linksModel: NewsLinkStore) extends Actor {
  //implicit val node = Cluster(ScraperCluster.system1)
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case e: Exception => println("Exception detected by scraper")
      Restart
  }

  val deleter = context.system.actorOf(Props(classOf[DeleteActor], linksModel))

  def receive = {
    case StartScraping =>
      try {
        serviceParser.refreshFeed(tabId).map(links => {
          //println(s"mapped result for $tabId")
          deleter ! FinishScraping(tabId, links)
        })
      } catch {
        case e: Exception => println("Error scraping")
      }
    case _ => println("Unexpected msg to Scraper")
  }
}

case class FinishScraping(tabId: String, links: Seq[TitleLink])
case object StartScraping

