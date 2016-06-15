package spa.client.modules

import diode.data.Pot
import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import spa.client.SPAMain.{FeedbackLoc, Loc}
import spa.client.services.Todos
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spa.client.components.Bootstrap._
import spa.client.components._
import spa.client.logger._
import spa.client.services._
import spa.shared._

/**
  * Eventually will be a simple about page with a link to a feedback form
  */

object About {
  case class Props(router: RouterCtl[Loc])
  @inline private def bss = GlobalStyles.bootstrapStyles

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props) = {//}, s: State) = {
      <.div(^.className:="col-md-10 col-md-offset-1")(
        Panel(Panel.Props("About internet-abridged.com"),
          <.div(
            <.p(
              """I created this site to learn React.js to create the UI for a "single page app"
                |and create an aggregator for myself entirely built in Scala.  All the JavaScript, HTML,
                |and backend server code used for this site are written in the same language, leveraging a
                |library for the HTML fragments, and the Scala.js compiler to compile Scala to browser JS
                |instead of server side JVM code.  Server side, I make use of a small set of Akka Actors to
                |concurrently retrieve updated feeds roughly every fifteen minutes.""".stripMargin),
            <.div(
              "Do you have questions about the site?  Suggestions for feeds you think I should aggregate?  ",
              "Send me some ", p.router.link(FeedbackLoc)("feedback.  "),
              "Interested in me as a candidate to work at your company?  ",
              "Let's see if we'd make a good fit for one another.  ",
              p.router.link(FeedbackLoc)("You can reach me the same way.")
            )
          )
        )
      )
    }
  }

  val component = ReactComponentB[Props]("About")
    .initialState({
      log.debug("About init")
      })
    .renderBackend[Backend]
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc]) = component(Props(router))
}
