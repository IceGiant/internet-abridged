package spa.client

import japgolly.scalajs.react.{Callback, ReactDOM}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spa.client.components.GlobalStyles
import spa.client.logger._
import spa.client.modules._
import spa.client.services.SPACircuit

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object HomeLoc extends Loc

  //case object TodoLoc extends Loc

  case object AboutLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    // wrap/connect components to the circuit
    (staticRoute(root, HomeLoc) ~> renderR(ctl => Home(ctl))
      //| staticRoute("#todo", TodoLoc) ~> renderR(ctl => SPACircuit.connect(_.todos)(Todo(_)))
      | staticRoute("#info", AboutLoc) ~> renderR(ctl => SPACircuit.connect(_.todos)(About(_)))
      ).notFound(redirectToPage(HomeLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(<.a(^.href:="#", ^.className := "navbar-brand", "The Internet (Abridged)"))),
          <.div(^.className := "collapse navbar-collapse",
            // connect menu to model, because it needs to update when the number of open todos changes
            /*SPACircuit.connect(_.todos.map(_.items.count(!_.completed)).toOption)(proxy => */MainMenu(c, r.page)//, proxy))
          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
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
