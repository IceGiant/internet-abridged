package spa.client.modules

import diode.Circuit
import diode.react._
import diode.react.ReactPot._
import diode.data.{Empty, Pot}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.{HTMLAnchorElement, HTMLDivElement, HTMLUListElement}
import spa.client.SPAMain._
import spa.client.components.Bootstrap._
import spa.client.components._
import spa.client.logger._
import spa.client.modules
import spa.client.modules.HomeInits.TabState
import spa.client.services.NewsCircuit._
import spa.client.components.Icon._
import spa.client.services.{NewsCircuit, _}
import spa.shared.TabId._
import spa.shared.{TabId, LinkObject, TabFeedSources}

import scalacss.ScalaCssReact._


object Home {

  case class Props(router: RouterCtl[Loc])
  case class State(search: String = "")

  class Backend($: BackendScope[Props, State]) {

    def onTextChange(e: ReactEventI) = {
      val text = e.target.value
      //log.debug(s"Search changed to ${text}")
      val cb = Callback.log(s"Search changed to ${text}")
      cb >> $.modState(s => s.copy(search = text))
    }

    val searchField = <.input.text(GlobalStyles.bootstrapStyles.formControl, ^.id := "search", ^.placeholder := "Search...", ^.onChange==>onTextChange)

    def render(p: Props, s: State) = {
      <.div(^.paddingTop:="10")(
        //searchField,
        SectionsByTopic(s.search)

        // create a link to the To Do view
        //<.div(router.link(TodoLoc)("Check your todos!"))
      )
    }
  }
  // create the React component for Home
  private val component = ReactComponentB[Props]("Home")
      .initialState(State(""))
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}

object SectionsByTopic{
  case class Props(search: String)

  case class LinksModel(links: Pot[Links])

  class LinksCircuit extends Circuit[LinksModel] with ReactConnector[LinksModel] {
    // initial application model
    override protected def initialModel = LinksModel(Empty)
    // combine all handlers into one
    override protected val actionHandler = combineHandlers(
      new LinkHandler(zoomRW(_.links)((m, v) => m.copy(links = v)))
    )
  }

  val redditTabs = HomeInits.generateSubreddits
  val redditCircuit = new LinksCircuit

  val techTabs = HomeInits.generateTech
  val techCircuit = new LinksCircuit


  val programmingTabs = HomeInits.generateProgramming
  val ProgrammingCircuit = new LinksCircuit


  val imageTabs = HomeInits.generateImages
  val ImageCircuit = new LinksCircuit


  private val component = ReactComponentB[Props]("SearchableComponent")
    .render_P { case Props(search) => {
      //Connect sections up to their circuits so calls to the server or searches reflect to this part of the UI
      val redditComponent = redditCircuit.connect(_.links)(p =>
        TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.reddit, "Reddit", redditTabs, startLinksHidden = false, searchFilter = search)))
      val techComponent = techCircuit.connect(_.links)(p =>
        TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.tech, "Tech", techTabs, searchFilter = search)))
      val ProgrammingComponent = ProgrammingCircuit.connect(_.links)(p =>
        TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.programming, "Programming", programmingTabs, searchFilter = search)))
      val ImageComponent = ImageCircuit.connect(_.links)(p =>
        TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.images, "Images", imageTabs, searchFilter = search)))

      <.div(^.paddingTop:="10")(
        redditComponent,
        techComponent,
        ImageComponent,
        ProgrammingComponent
      )}
    }.build

  def apply(search: String) = component(Props(search))
}


object TabbedLinkContainer{

  @inline private def bss = GlobalStyles.bootstrapStyles
  case class Props(proxy: ModelProxy[Pot[Links]], id: String, sectionName: String,
                   tabs: List[ContainerTab.Props], startLinksHidden: Boolean = true, searchFilter: String = "")
  case class StateProps(tabId: String, linksHidden: Boolean = false)

  class Backend($: BackendScope[Props, StateProps]) {
    def mounted(props: Props, state: StateProps) = {
      val cb = Callback.ifTrue(props.proxy().isEmpty, {props.proxy.dispatch(RefreshLinks(state.tabId))})
      cb >> $.modState(s => s.copy(linksHidden = props.startLinksHidden))
    }

    def selectTab(tab: String) = {
      val cb = Callback.log(s"selected ${tab}") >> $.props >>= (_.proxy.dispatch(RefreshLinks(tab)))
      cb >> $.modState(s => {
        $.props.map(p => HomeInits.updateCache(TabState(p.id, tab)))
        s.copy(tabId = tab, linksHidden = false)
      })
    }

    def collapseExpand = {
      val cb = Callback.log(s"Section collapsed")
      cb >> $.modState(s => s.copy(linksHidden = !s.linksHidden))
    }

    def coloredCollapseButton(isCollapsed: Boolean) = {
      <.button(bss.pullRight, bss.button, ^.borderRadius:="0 !important",
        if (isCollapsed) ^.background:="#222"
        else ^.background:="#000000",
        if (isCollapsed) ^.color:="#9d9d9d"
        else ^.color:="#ffffff",
        ^.paddingLeft:="4",
        ^.paddingRight:="4",
        ^.paddingBottom:="0",
        ^.height:="50",
        ^.width:="50",
        ^.onClick --> collapseExpand)(
        <.h3(^.marginTop:="3", ^.marginBottom:="9", ^.marginTop:="3")(
          if (isCollapsed) Icon.caretDown
          else Icon.caretUp
        )
      )
    }

    def render(p: Props, s: StateProps) = {
      <.div(
          ^.id := p.id, bss.panel,
      <.nav(^.id := "tn", ^.className := "navbar navbar-inverse", ^.marginBottom:="0", ^.borderRadius:="0 !important")(
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", p.sectionName)), <.ul(^.className := "nav navbar-nav")(
            {
              for (props <- p.tabs) yield {
                  ContainerTab(ContainerTab.Props(props.anchor, props.img, props.tabId, props.style, props.onSelectedUrl, selectTab, props.tabId==s.tabId))
              }
            }
          ),
        coloredCollapseButton(s.linksHidden)
      ),

        <.div(^.borderRadius:="0 !important")(
          p.proxy().renderFailed(ex => {
            log.debug(s"${p.sectionName} error")
            <.p("Error loading")
          }),
          p.proxy().renderPending(_ > 500, _ => {
            log.debug(s"${p.sectionName} loading")
            <.p("Loading...")
          }),
          p.proxy().render(links => {
            //log.debug(s"${p.sectionName} links count: ${links.items.length}")
            LinkList(links.items, s.linksHidden, p.searchFilter)
          })
        )
      )
    }
  }

  val TabbedLinkContainer = ReactComponentB[Props]("ListTabHeader")
    .initialState_P(
      props => {
        val state = HomeInits.getState(props.id)
      //log.debug(s"initial state set to ${state}")
      StateProps(state)})
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props, scope.state))
    .build

  def apply(props: Props) = TabbedLinkContainer(props)
}

object ContainerTab {
  case class AnchorProps(id: String, href: String = "JavaScript:void()", rel: String = "nofollow")
  case class ImageProps(src: String, alt: String, border: String = "0", icon: ReactNode = <.span(), imgText: String = "")

  case class Props(anchor: AnchorProps,
                   img: ImageProps,
                   tabId: String,
                   style: String = "",
                   onSelectedUrl: String,
                   changeState: String => Callback = null,
                   selected: Boolean = false)

  val listTab = ReactComponentB[Props]("ListTab")
    .render_P(p =>{
        <.li(^.id:=p.tabId, if (p.selected) ^.className:="nav-item active" else ^.className:="nav-item")(
          <.a(^.id:=p.anchor.id,
            ^.href:=p.anchor.href,
            ^.rel:=p.anchor.rel) (
            p.img.icon, " ", p.img.imgText
          ), ^.onClick --> p.changeState(p.tabId)
        )
    })
    .build

  def apply(props: Props) = listTab(props)
}

object LinkList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(
                    items: Seq[LinkObject],
                    hidden: Boolean = false,
                    titleFilter: String = ""
                  )

  private val linkList = ReactComponentB[Props]("LinkList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: LinkObject) = {
        if (item.title.toLowerCase.contains(p.titleFilter.toLowerCase)) {
          <.li(bss.listGroup.item, ^.borderRadius := "0 !important")(
            <.a(^.href := item.href,
              ^.rel := "nofollow",
              ^.target := "external"
            )(
              item.title
            )
          )
        }
        else <.span
      }
      if (p.items.nonEmpty) {
        <.ul(^.marginBottom := "0", bss.listGroup.listGroup, ^.borderRadius := "0 !important")(
          !p.hidden ?= p.items map renderItem
        )
      }
      else {
        <.p("Couldn't pull any data")
      }
    })
    .build

  def apply(items: Seq[LinkObject], hidden: Boolean, searchFilter: String) =
    linkList(Props(items, hidden, searchFilter))
}

