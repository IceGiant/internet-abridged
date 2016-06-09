package actors

import akka.actor.{Actor, Props}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object InsertActor {
  def props = Props(new InsertActor)
}

class InsertActor extends Actor {
  def receive = {
    case FinishDeletion(tab, links, result) =>
      try {
        models.NewsLinkModel.store.create(tab, links)
      } catch {
        case e: Exception => println("Exception during insert")
      }
    case _ =>
  }
}