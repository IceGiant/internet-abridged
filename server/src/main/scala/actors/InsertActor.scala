package actors

import akka.actor.{Actor, Props}
import models.{NewsLinkModel, NewsLinkStore}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object InsertActor {
  def props(linksModel: NewsLinkStore) = Props(new InsertActor(linksModel: NewsLinkStore))
}

class InsertActor(linksModel: NewsLinkStore) extends Actor {
  def receive = {
    case FinishDeletion(tab, links, result) =>
      try {
        linksModel.create(tab, links)
      } catch {
        case e: Exception => println("Exception during insert")
      }
    case _ =>
  }
}