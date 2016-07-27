package spa.client.modules

import diode.Circuit
import diode.react._
import diode.react.ReactPot._
import diode.data.{Empty, Pot}
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import spa.client.SPAMain._
import spa.client.components.Bootstrap.{Button, Panel}
import spa.client.components.GlobalStyles
import spa.client.logger._
import spa.client.services.{FeedbackHandler, FeedbackResponse, SendFeedback}
import spa.shared.{EmailFormData, EmailValidation}

/**
  * Created by skypage on 6/14/16.
  */
object Feedback {
  case class Props(router: RouterCtl[Loc])


  case class FeedbackResponseModel(sent: Pot[FeedbackResponse])

  object FeedbackCircuit extends Circuit[FeedbackResponseModel] with ReactConnector[FeedbackResponseModel] {
    // initial application model
    override protected def initialModel = FeedbackResponseModel(Empty)
    // combine all handlers into one
    override protected val actionHandler = composeHandlers(
      new FeedbackHandler(zoomRW(_.sent)((m, v) => m.copy(sent = v)))
    )
  }

  val feedbackWrapper = FeedbackCircuit.connect(_.sent)

  private val component = ReactComponentB[Props]("SearchableComponent")
    .render_P { p =>
      feedbackWrapper(FeedbackForm(_))
    }.build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc]) = component(Props(router))
}

object FeedbackForm {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(proxy: ModelProxy[Pot[FeedbackResponse]])
  case class State(name: String = "",
                   message: String = "",
                   subject: String = "",
                   email: String = "",
                   validInput: Boolean = true,
                   inputMessage: String = "",
                   submitDisabled: Boolean = false,
                   submitClicked: Boolean = false
                  )

  class Backend($: BackendScope[Props, State]) {

    def onSubmit (proxy: ModelProxy[Pot[FeedbackResponse]]) = {
      var validInput: Boolean = true

      $.modState( s => {
        if (s.subject.trim.isEmpty) {
          validInput = false
          s.copy(validInput = false, inputMessage = "You're missing a subject", submitClicked = true)
        }
        else if (s.message.trim.isEmpty) {
          validInput = false
          s.copy(validInput = false, inputMessage = "You're missing a message", submitClicked = true)
        }
        else if (s.email.trim.nonEmpty) {
          if (EmailValidation.isValid(s.email)) {
            s.copy(validInput = true, inputMessage = "", submitDisabled = true, submitClicked = true)
          }
          else {
            validInput = false
            s.copy(validInput = false, inputMessage = "Your email is invalid", submitClicked = true)
          }
        }
        else {
          s.copy(validInput = true, inputMessage = "", submitDisabled = true, submitClicked = true)
        }
    }) >> $.state >>= {s =>
        //This chains the state modification Callback to the Callback that communicates with the server
        if (validInput) {
          val submitData = EmailFormData(s.name, s.email, s.subject, s.message)
          proxy.dispatch(SendFeedback(submitData))
        }
        else Callback()
      }
  }

    def onSubjectChange(e: ReactEventI) = {
      val text = e.target.value.toString
      log.debug(text)
      $.modState(s => {
        s.copy(subject = text)
      })
    }

    def onMessageChange(e: ReactEventI) = {
      val text = e.target.value.toString
      log.debug(text)
      $.modState(s => {
        s.copy(message = text)
      })
    }

    def onNameChange(e: ReactEventI) = {
      val text = e.target.value.toString
      log.debug(text)
      $.modState(s => {
        s.copy(name = text)
      })
    }

    def onEmailChange(e: ReactEventI) = {
      val text = e.target.value.toString
      log.debug(text)
      $.modState(s => {
        s.copy(email = text)
      })
    }


    def render(p: Props, s: State) = {
      <.div(^.className:="col-md-8 col-md-offset-2")(
        Panel(Panel.Props("Feedback"),
          <.form()(
            <.div(^.className:="form-group")(
              <.label(^.`for`:="subject")("Subject:"), <.br,
              <.input.text(^.id:="subject", ^.className:="form-control", ^.onChange ==> onSubjectChange), <.br,
              <.label(^.`for`:="message")("Message:"), <.br,
              <.textarea(^.id:="message", ^.className:="form-control", ^.rows:="5", ^.onChange ==> onMessageChange), <.br, <.br,

              <.b("Optional information:"),
              <.p()(
                """You may leave everything below blank to send a completely anonymous note,
                   or you may provide what information you like. I can't reply to your message
                   without an email address, so keep that in mind.  Your email address will only
                   be used as a possible way for me to reply to you."""),
              <.label(^.`for`:="name")("Your name:"), <.br,
              <.input.text(^.id:="name", ^.className:="form-control", ^.onChange ==> onNameChange), <.br,
              <.label(^.`for`:="email")("Your email:"), <.br,
              <.input.email(^.id:="email", ^.className:="form-control", ^.onChange ==> onEmailChange), <.br,
              Button(Button.Props(onSubmit(p.proxy), addStyles = Seq(bss.pullRight, bss.button), disabled = s.submitDisabled), "Submit"),
              <.div(
                if (s.submitClicked) {
                  if (s.validInput) {
                    <.span(
                      p.proxy().renderFailed(ex => <.p("Load failed")), p.proxy().renderPending(pend => <.p("Loading...")),
                      p.proxy().render(result =>
                        if (result.sent) {
                          <.p("Success!  Thanks for your input!")
                        }
                        else <.p("Submitting your message...")
                      )
                    )
                  }
                  else {
                    <.span(
                      <.p(^.color := "red")("Looks like you made a mistake:"),
                      <.p(^.color := "red")(s.inputMessage)
                    )
                  }
                } else <.span
              )
            )
          )
        )
      )
    }

  }

  val component = ReactComponentB[Props]("Feedback")
    .initialState({
      State()
    })
    .renderBackend[Backend]
    .build

  def apply(sent: ModelProxy[Pot[FeedbackResponse]]) = component(Props(sent))
}
