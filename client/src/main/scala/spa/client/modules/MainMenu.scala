package spa.client.modules

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spa.client.SPAMain._
import spa.client.components.Bootstrap.CommonStyle
import spa.client.components.Icon._
import spa.client.components._
import spa.client.services._
import spa.shared.FeedIds._
import org.scalajs.jquery.{jQuery => $}
import org.scalajs.dom

import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc = HomeLoc)//, proxy: ModelProxy[Option[Int]])

  private case class MenuItem(idx: Int, label: (Props) => ReactNode, icon: Icon, location: Loc)

  def themeClick(name: String) = {
    val theme = "//netdna.bootstrapcdn.com/bootswatch/3.3.5/" + name + "/bootstrap.min.css"
    setTheme(theme)
    Callback.log(s"Theme changed to $name")
  }

  val supportsLocalStorage = supportHtml5Storage

  def setTheme(theme: String) = {
    $("""link[title="main"]""").attr("href", theme)
    if (supportsLocalStorage) dom.window.localStorage.setItem("theme", theme)
  }

  def supportHtml5Storage = {
    try {
      dom.window.localStorage != null
    } catch {
      case e: Exception => false
    }
  }

  if (supportsLocalStorage) {
    val theme = dom.window.localStorage.getItem("theme")
    if (theme.nonEmpty && theme != null) setTheme(theme)
  } else {
    $("#theme-dropdown").hide()
  }

  private val menuItems = Seq(
    MenuItem(1, _ => "Home", Icon.home, HomeLoc),
    MenuItem(2, _ => "About", Icon.info, AboutLoc)
  )


  private val themesDropdown =
    <.li(^.className := "dropdown", ^.id := "theme-dropdown")(
      <.a(
        ^.className := "dropdown-toggle", "data-toggle".reactAttr := "dropdown", ^.role := "button"
      )("Themes", " ", Icon.caretDown),
      <.ul(^.className := "dropdown-menu", ^.role := "menu")(
        <.li(<.a(^.href := "#", ^.onClick --> themeClick("cyborg"))("Cyborg (Default)")),
        <.li(<.a(^.href := "#", ^.onClick --> themeClick("cerulean"))("Cerulean")),
        <.li(<.a(^.href := "#", ^.onClick --> themeClick("cosmo"))("Cosmo")),
        <.li(<.a(^.href := "#", ^.onClick --> themeClick("slate"))("Slate")),
        <.li(<.a(^.href := "#", ^.onClick --> themeClick("spacelab"))("Spacelab"))
      )
    )

  private class Backend($: BackendScope[Props, Unit]) {
    /*def mounted(props: Props) =
      // dispatch a message to refresh the todos
      Callback.ifTrue(props.proxy.value.isEmpty, props.proxy.dispatch(RefreshTodos))*/

    def render(props: Props) = {
      <.ul(bss.navbar)(
        // build a list of menu items
        for (item <- menuItems) yield {
          <.li(^.key := item.idx,
            if (props.currentLoc == item.location) ^.className := "nav-item active"
            else ^.className := "nav-item",
            props.router.link(item.location)(item.icon, " ", item.label(props))
          )
        }, themesDropdown
      )
    }
  }

  private val component = ReactComponentB[Props]("MainMenu")
    .renderBackend[Backend]
    //.componentDidMount(scope => {scope.backend.mounted(scope.props)})
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc/*, proxy: ModelProxy[Option[Int]]*/): ReactElement =
    component(Props(ctl, currentLoc/*, proxy*/))
}
