package actors

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props}
import models.{NewsLinkModel, NewsLinkStore}
import services.TitleLink

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

case class FinishDeletion(tabId: String, links: Seq[TitleLink], deleteResult: Boolean)


object DeleteActor {
  def props(linksModel: NewsLinkStore) = Props(new DeleteActor(linksModel: NewsLinkStore))
}

class DeleteActor(linksModel: NewsLinkStore) extends Actor{
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case e: Exception => println("Exception detected by deleter")
      Restart
  }

  val inserter = context.system.actorOf(Props(classOf[InsertActor], linksModel))

  def receive = {
    case FinishScraping(tab, links) =>
      try {
        linksModel.delete(tab).map(result => {
          inserter ! FinishDeletion(tab, links, result)
        })
      } catch {
        case e: Exception => println("Exception while inserting")
      }
    case _ =>
  }
}