package spa.client

import japgolly.scalajs.react.{Callback, ReactDOM}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spa.client.components.{GlobalStyles, Icon}
import spa.client.logger._
import spa.client.modules._
import spa.client.services._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExportTopLevel("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object HomeLoc extends Loc

  case object AboutLoc extends Loc

  case object FeedbackLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    // wrap/connect components to the circuit
    (staticRoute(root, HomeLoc) ~> renderR(ctl => Home(ctl))
      | staticRoute("#about", AboutLoc) ~> renderR(ctl => About(ctl))
      | staticRoute("#feedback", FeedbackLoc) ~> renderR(ctl => Feedback(ctl))
      ).notFound(redirectToPage(HomeLoc)(Redirect.Replace))
  }.renderWith(layout)

  def settingsButton(divId: String) = {
    <.button(^.`type` := "button",
      ^.className := "navbar-toggle",
      ^.borderRadius := "0px",
      ^.padding := "4px",
      ^.marginTop := "0px",
      ^.marginBottom := "0px",
      ^.marginRight := "0px",
      ^.marginLeft := "5px",
      //^.background := "#000000",
      //^.color := "#9d9d9d",
      ^.height := "50px",
      ^.width := "50px",
      "data-toggle".reactAttr := "collapse",
      "data-target".reactAttr := s"#${divId}")(
      Icon.bars
    )
  }


  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(

      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-default navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(<.a(^.href:="#", ^.className := "navbar-brand", "The Internet (Abridged)")))
          (
            settingsButton("mainNavigation")
          ),
          <.div(^.id := "mainNavigation", ^.className := "collapse navbar-collapse",
            MainMenu(c, r.page)//, proxy))
          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExportTopLevel("main")
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
