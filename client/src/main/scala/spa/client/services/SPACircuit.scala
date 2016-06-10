package spa.client.services

import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import spa.shared.{Api, LinkObject, TodoItem}
import boopickle.Default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions
/*case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}*/

case class UpdateListOfLinks(links: Seq[LinkObject])

case class RefreshLinks(tabId: String, contentType: String = "article")

case class Links(items: Seq[LinkObject]) {
  def updated(newItem: LinkObject) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Links(items :+ newItem)
      case idx =>
        // replace old
        Links(items.updated(idx, newItem))
    }
  }
  def remove(item: LinkObject) = Links(items.filterNot(_ == item))
}


// The base model of our application
case class RootModel(motd: Pot[String], links: Pot[Links])
case class RootNewsModel(newsLinks: Pot[Links], testLinks: Pot[Links])

case class Todos(items: Seq[TodoItem]) {
  def updated(newItem: TodoItem) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Todos(items :+ newItem)
      case idx =>
        // replace old
        Todos(items.updated(idx, newItem))
    }
  }
  def remove(item: TodoItem) = Todos(items.filterNot(_ == item))
}

/**
  * Handles actions related to scraped lists of links
  *
  * @param modelRW Reader/Writer to access the model
  */

class LinkHandler[M](modelRW: ModelRW[M, Pot[Links]]) extends ActionHandler(modelRW) {
  override def handle = {
    case UpdateListOfLinks(links) =>
      updated(Ready(Links(links)))
    case RefreshLinks(tab, contentType) =>
      effectOnly(Effect(AjaxClient[Api].updateNewsList(tab, contentType).call().map(UpdateListOfLinks)))
  }
}


// Application circuit

object NewsCircuit extends Circuit[RootNewsModel] with ReactConnector[RootNewsModel] {
  // initial application model
  override protected def initialModel = RootNewsModel(Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = combineHandlers(
    new LinkHandler(zoomRW(_.newsLinks)((m, v) => m.copy(newsLinks = v))),
    new LinkHandler(zoomRW(_.testLinks)((m, v) => m.copy(testLinks = v)))
  )
}