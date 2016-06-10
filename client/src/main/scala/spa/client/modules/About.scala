package spa.client.modules

import diode.data.Pot
import diode.react.ModelProxy
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
  case class Props(proxy: ModelProxy[Pot[Links]])
  case class State(filler: String = "")
  @inline private def bss = GlobalStyles.bootstrapStyles

  class Backend($: BackendScope[Props, Unit]) {

    def onSubmit= {
      Callback.log("")
    }

    def render(p: Props) = {//}, s: State) = {
      <.div(
        Panel(Panel.Props("Nothing here quite yet.  Coming soon!"))/*,
        <.form()(
          Panel(Panel.Props(""),
          //Potential feedback form beginning
            <.input.text(^.id:="name"),
            <.input.text(^.id:="email"),
            <.textarea(^.id:="message"),
            Button(Button.Props(onSubmit, addStyles = Seq(bss.pullRight)), "Submit")
          )
        )*/
      )
    }

  }

  val component = ReactComponentB[Props]("TODO")
    .initialState({
      log.debug("todo initial")
      })
    .renderBackend[Backend]
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Links]]) = component(Props(proxy))
}
