package spa.client.services

import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import spa.shared.{Api, EmailFormData, LinkObject}
import boopickle.Default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


case class RefreshLinks(tabId: String, contentType: String = "article") extends Action
case class UpdateListOfLinks(links: Seq[LinkObject]) extends Action
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


case class SendFeedback(feedbackDate: EmailFormData) extends Action
case class FeedbackSent(sent: Boolean) extends Action
case class FeedbackResponse(sent: Boolean) {
  def updated(value: Boolean) = FeedbackResponse(value)
}


// The base model of our application
case class RootModel(motd: Pot[String], links: Pot[Links])
case class RootNewsModel(newsLinks: Pot[Links], testLinks: Pot[Links])



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

/**
  * Handles actions related to feedback submission
  *
  * @param modelRW Reader/Writer to access the model
  */

class FeedbackHandler[M](modelRW: ModelRW[M, Pot[FeedbackResponse]]) extends ActionHandler(modelRW) {
  override def handle = {
    case FeedbackSent(response) =>
      updated(Ready(FeedbackResponse(response)))
    case SendFeedback(feedback) =>
      effectOnly(Effect(AjaxClient[Api].submitFeedback(feedback).call().map(FeedbackSent)))

  }
}




// Application circuit

object NewsCircuit extends Circuit[RootNewsModel] with ReactConnector[RootNewsModel] {
  // initial application model
  override protected def initialModel = RootNewsModel(Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new LinkHandler(zoomRW(_.newsLinks)((m, v) => m.copy(newsLinks = v))),
    new LinkHandler(zoomRW(_.testLinks)((m, v) => m.copy(testLinks = v)))
  )
}