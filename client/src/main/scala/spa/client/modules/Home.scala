package spa.client.modules

import diode.Circuit
import diode.react._
import diode.react.ReactPot._
import diode.data.{Empty, Pot}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spa.client.SPAMain._
import spa.client.components.Bootstrap._
import spa.client.components._
import spa.client.logger._
import spa.client.modules
import spa.client.modules.HomeInits.TabState
import spa.client.components.Icon._
import spa.client.services.{NewsCircuit, _}
import spa.shared.FeedIds._
import spa.shared.{FeedIds, LinkObject, FeedUrls}

import scalacss.ScalaCssReact._

/**
  * Top level component for the homepage
  */
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

    val searchField = <.input.text(GlobalStyles.bootstrapStyles.formControl,
      ^.id := "search", ^.placeholder := "Search...", ^.onChange ==> onTextChange)

    def render(p: Props, s: State) = {
      <.div(^.paddingTop:="10px")(
        <.meta(^.name := "description",
          ^.contentAttr := "The Net is vast and infinite, but some of the best parts are aggregated here."),
        //searchField,
        SectionsByTopic(s.search)
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

/**
  * Sections by general topic, with a search term passed in for easy filtering when looking
  * for a particular topic
  */
object SectionsByTopic{
  case class Props(search: String)

  case class LinksModel(links: Pot[Links])

  class LinksCircuit extends Circuit[LinksModel] with ReactConnector[LinksModel] {
    // initial application model
    override protected def initialModel = LinksModel(Empty)
    // combine all handlers into one
    override protected val actionHandler = composeHandlers(
      new LinkHandler(zoomRW(_.links)((m, v) => m.copy(links = v)))
    )
  }

  val redditTabs = HomeInits.generateSubreddits
  val redditCircuit = new LinksCircuit

  val techTabs = HomeInits.generateTech
  val techCircuit = new LinksCircuit

  val comicTabs = HomeInits.generateComics
  val ComicCircuit = new LinksCircuit

  val programmingTabs = HomeInits.generateProgramming
  val ProgrammingCircuit = new LinksCircuit

  val podcastTabs = HomeInits.generatePodcasts
  val podcastCircuit = new LinksCircuit


  private val component = ReactComponentB[Props]("SearchableComponent")
    .render_P { case Props(search) => {
      //Connect sections up to their circuits so calls to the server or searches reflect to this part of the UI
      val redditWrapper = redditCircuit.connect(_.links)
      val techWrapper = techCircuit.connect(_.links)
      val programmingWrapper = ProgrammingCircuit.connect(_.links)
      val comicWrapper = ComicCircuit.connect(_.links)
      val podcastWrapper = podcastCircuit.connect(_.links)

      <.div(^.paddingTop:="10px")(
        redditWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.reddit, "Reddit", redditTabs, startLinksHidden = false, searchFilter = search))
        ),
        techWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.tech, "Tech", techTabs, searchFilter = search))
        ),
        comicWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.comics, "Comics", comicTabs, searchFilter = search)))
        ,
        programmingWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.programming, "Programming", programmingTabs, searchFilter = search))
        ),
        podcastWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.podcasts, "Podcasts", podcastTabs, searchFilter = search))
        )
      )}
    }.build

  def apply(search: String) = component(Props(search))
}

/**
  * Container of selectable tabs in a nav which update the links below the nav to reflect the selection
  */

object TabbedLinkContainer{

  @inline private def bss = GlobalStyles.bootstrapStyles
  case class Props(proxy: ModelProxy[Pot[Links]], id: String, sectionName: String,
                   tabs: List[TabContainer.Props], startLinksHidden: Boolean = true, searchFilter: String = "")
  case class StateProps(tabId: String, linksHidden: Boolean = false)

  class Backend($: BackendScope[Props, StateProps]) {
    def mounted(props: Props, state: StateProps) = {
      val cb = Callback.when(props.proxy().isEmpty)({props.proxy.dispatch(RefreshLinks(state.tabId))})
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
      <.button(bss.pullRight, bss.button, ^.borderRadius:="0px !important",
        if (isCollapsed) ^.background:="#222"
        else ^.background:="#000000",
        if (isCollapsed) ^.color:="#9d9d9d"
        else ^.color:="#ffffff",
        ^.borderRadius := "0px",
        ^.padding := "4px",
        ^.paddingBottom := "0px",
        ^.height := "50px",
        ^.width := "50px",
        ^.onClick --> collapseExpand)(
        <.h3(^.marginTop := "3px", ^.marginBottom := "9px", ^.marginTop := "3px")(
          if (isCollapsed) Icon.caretDown
          else Icon.caretUp
        )
      )
    }

    def settingsButton(divId: String) = {
      <.button(^.`type` := "button",
        ^.className := "navbar-toggle",
        ^.borderRadius := "0px",
        ^.padding := "4px",
        ^.marginTop := "0px",
        ^.marginBottom := "0px",
        ^.marginRight := "0px",
        ^.marginLeft := "5px",
        ^.background := "#000000",
        ^.color := "#9d9d9d",
        ^.height := "50px",
        ^.width := "50px",
        "data-toggle".reactAttr := "collapse",
        "data-target".reactAttr := s"#${divId}-items")(
        Icon.bars
      )
    }

    def render(p: Props, s: StateProps) = {
      <.div(^.borderRadius:="0px !important", ^.marginBottom := "20px")(
        ^.id := p.id,
        <.nav(^.id := "tn", ^.className := "navbar navbar-inverse", ^.marginBottom:="0px", ^.borderRadius:="0px")(
          <.div(^.className := "row")(
            <.div(^.className := "col-sm-11")(
              <.span(^.className := "navbar-header", ^.borderRadius:="0px !important",
                <.span(^.className := "navbar-brand", p.sectionName))
              (
                settingsButton(p.id),
                if (dom.window.innerWidth < 768) { //Only show collapse button here on mobile browsers
                  <.span("data-toggle".reactAttr := "collapse")(
                    coloredCollapseButton(s.linksHidden))
                }
                else <.span
              ),
              <.span(^.id := s"${p.id}-items", ^.className := "collapse navbar-collapse", ^.borderRadius:="0px !important")(
                <.ul(^.className := "nav navbar-nav", ^.borderRadius:="0px !important")(
                  for (props <- p.tabs) yield {
                    TabContainer(TabContainer.Props(props.anchor, props.img, props.tabId, props.style, props.onSelectedUrl, selectTab, props.tabId == s.tabId))
                  }
                )
              )
            ),
            if (dom.window.innerWidth > 768) { //Only show collapse button here on desktops
              <.div(^.className := "col-sm-1")(
                coloredCollapseButton(s.linksHidden)
              )
            }
            else <.span
          )
        ),

        <.div(^.borderRadius := "0px !important")(
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


/**
  * Individual "tab" on one of the link container navs
  */
object TabContainer {
  case class AnchorProps(id: String = "", href: String = "JavaScript:void()", rel: String = "nofollow")
  case class ImageProps(src: String, alt: String, border: String = "0px", icon: ReactNode = <.span(), imgText: String = "")

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

/**
  * Create a list of links scraped for the selected tab and make them presentable
  */
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
          <.li(bss.listGroup.item, ^.borderRadius := "0px")(
            <.a(^.href := item.href,
              ^.rel := "nofollow",
              ^.target := s"external-${UniqueTarget()}"
            )(
              item.title
            )
          )
        }
        else <.span
      }
      if (p.items.nonEmpty) {
        <.ul(^.marginBottom := "0", bss.listGroup.listGroup, ^.borderRadius := "0px")(
          !p.hidden ?= p.items map renderItem
        )
      }
      else {
        <.li(bss.listGroup.item, ^.borderRadius := "0px")("Couldn't pull any data")
      }
    })
    .build

  def apply(items: Seq[LinkObject], hidden: Boolean, searchFilter: String) =
    linkList(Props(items, hidden, searchFilter))
}

object UniqueTarget {
  var counter: Int = 0
  def apply(): Int = {
    counter += 1
    counter
  }
}