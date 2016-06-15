package spa.client.modules

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentB, Callback, BackendScope}
import japgolly.scalajs.react.extra.router.RouterCtl
import spa.client.SPAMain.Loc
import spa.client.components.Bootstrap.{Button, Panel}
import spa.client.components.GlobalStyles
import spa.client.logger._

/**
  * Created by skypage on 6/14/16.
  */
object Feedback {
  case class Props(router: RouterCtl[Loc])
  @inline private def bss = GlobalStyles.bootstrapStyles

  class Backend($: BackendScope[Props, Unit]) {

    def onSubmit= {
      Callback.log("Testing")
    }

    def render(p: Props) = {//}, s: State) = {
      <.div(^.className:="col-md-8 col-md-offset-2")(
        Panel(Panel.Props("Feedback"),
          <.form()(
            <.div(^.className:="form-group")(
              <.label(^.`for`:="subject")("Subject:"), <.br,
              <.input.text(^.id:="subject", ^.className:="form-control")(), <.br,
              <.label(^.`for`:="message")("Message:"), <.br,
              <.textarea(^.id:="message", ^.className:="form-control", ^.rows:="5")(), <.br, <.br,

              <.b("Optional information:"),
              <.p()(
                """You may leave everything below blank to send a completely anonymous note,
                   or you may provide what information you like. I can't reply to your message
                   without an email address, so keep that in mind.  Your email address will only
                   be used as a possible way for me to reply to you."""),
              <.label(^.`for`:="name")("Your name:"), <.br,
              <.input.text(^.id:="name", ^.className:="form-control"), <.br,
              <.label(^.`for`:="email")("Your email:"), <.br,
              <.input.email(^.id:="email", ^.className:="form-control"), <.br,

              Button(Button.Props(onSubmit, addStyles = Seq(bss.pullRight, bss.button)), "Submit")
            )
          ))
      )
    }

  }

  val component = ReactComponentB[Props]("Feedback")
    .initialState({
      log.debug("Feedback init")
    })
    .renderBackend[Backend]
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc]) = component(Props(router))
}
