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
import spa.shared.LinkType.LinkType
import spa.shared.LinkType.LinkType
import spa.shared.{LinkType, FeedIds, LinkObject, FeedUrls}

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

    def startPlaying(podcastLink: String) = {
      Callback.log("test")
    }

    val searchField = <.input.text(GlobalStyles.bootstrapStyles.formControl,
      ^.id := "search", ^.placeholder := "Search...", ^.onChange ==> onTextChange)

    def render(p: Props, s: State) = {
      <.div(^.paddingTop:="10px")(
        <.meta(^.name := "description",
          ^.contentAttr := "The Net is vast and infinite, but some of the best parts are aggregated here."),
        //searchField,
        SectionsByTopic(s.search, startPlaying)
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
  case class Props(search: String, podcastFn: (String) => Callback)

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
  val comicCircuit = new LinksCircuit

  val programmingTabs = HomeInits.generateProgramming
  val programmingCircuit = new LinksCircuit

  val securityTabs = HomeInits.generateSecurity
  val securityCircuit = new LinksCircuit

  val podcastTabs = HomeInits.generatePodcasts
  val podcastCircuit = new LinksCircuit


  private val component = ReactComponentB[Props]("SearchableComponent")
    .render_P { case Props(search, podcastFn) => {
      //Connect sections up to their circuits so calls to the server or searches reflect to this part of the UI
      val redditWrapper = redditCircuit.connect(_.links)
      val techWrapper = techCircuit.connect(_.links)
      val programmingWrapper = programmingCircuit.connect(_.links)
      val comicWrapper = comicCircuit.connect(_.links)
      val securityWrapper = securityCircuit.connect(_.links)
      val podcastWrapper = podcastCircuit.connect(_.links)

      <.div(^.paddingTop:="10px")(
        redditWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.reddit,
            "Reddit", redditTabs, startLinksHidden = false, searchFilter = search, linksType = LinkType.Article))
        ),
        techWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.tech,
            "Tech", techTabs, searchFilter = search, linksType = LinkType.Article))
        ),
        comicWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.comics,
            "Comics", comicTabs, searchFilter = search, linksType = LinkType.Article)))
        ,
        programmingWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.programming,
            "Programming", programmingTabs, searchFilter = search, linksType = LinkType.Article))
        ),
        securityWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.security,
            "Security", securityTabs, searchFilter = search, linksType = LinkType.Article))
        ),
        podcastWrapper(p =>
          TabbedLinkContainer(TabbedLinkContainer.Props(p, HomeInits.podcasts,
            "Podcasts", podcastTabs, searchFilter = search, linksType = LinkType.Podcast,
            podcastHandler = podcastFn))
        )
      )}
    }.build

  def apply(search: String, podcastFn: (String) => Callback) = component(Props(search, podcastFn))
}

/**
  * Container of selectable tabs in a nav which update the links below the nav to reflect the selected tab
  */

object TabbedLinkContainer{

  @inline private def bss = GlobalStyles.bootstrapStyles
  case class Props(proxy: ModelProxy[Pot[Links]],
                   id: String,
                   sectionName: String,
                   tabs: Iterable[TabContainer.Props],
                   startLinksHidden: Boolean = true,
                   searchFilter: String = "",
                   linksType: LinkType,
                   podcastHandler: (String) => Callback = null)
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
      <.button(bss.pullRight, bss.button, ^.borderRadius:="0px",
        //^.background := "#000000",
        //if (isCollapsed) ^.color:="#9d9d9d"
        //else ^.color:="#ffffff",
        ^.borderRadius := "0px",
        ^.padding := "0px",
        ^.paddingBottom := "0px",
        ^.height := "50px",
        ^.width := "50px",
        ^.onClick --> collapseExpand)(
        <.h3(^.marginTop := "3px", ^.marginBottom := "3px")(
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
        //^.background := "#000000",
        //^.color := "#9d9d9d",
        ^.height := "50px",
        ^.width := "50px",
        "data-toggle".reactAttr := "collapse",
        "data-target".reactAttr := s"#${divId}-items")(
        Icon.bars
      )
    }

    def panelBottomCollapse = {
      <.a(bss.button, ^.className := "col-xs-12", ^.borderRadius := "0px", ^.marginBottom := "0px", ^.height := "50px", ^.style := "width: 100%", ^.onClick --> collapseExpand)(
        <.h4(^.marginTop := "3px", ^.marginBottom := "3px", ^.textAlign := "center")(
          Icon.caretUp, "   Collapse   ", Icon.caretUp
        )
      )
    }

    def render(p: Props, s: StateProps) = {
      <.div(^.borderRadius:="0px", if (!s.linksHidden && dom.window.innerWidth < 768) ^.marginBottom := "70px" else ^.marginBottom := "20px")(
        ^.id := p.id,
        <.nav(^.id := "tn", ^.className := "navbar navbar-default", ^.marginBottom:="0px", ^.borderRadius:="0px")(
          <.div(^.className := "row")(
            <.div(^.className := "col-sm-11")(
              <.span(^.className := "navbar-header", ^.borderRadius:="0px",
                <.span(^.className := "navbar-brand", p.sectionName))
              (
                settingsButton(p.id),
                //Only show collapse button here on mobile browsers
                  <.span(bss.visibleXs, "data-toggle".reactAttr := "collapse")(
                    coloredCollapseButton(s.linksHidden))

              ),
              <.span(^.id := s"${p.id}-items", ^.className := "collapse navbar-collapse", ^.borderRadius:="0px")(
                <.ul(^.className := "nav navbar-nav", ^.borderRadius:="0px")(
                  for (props <- p.tabs) yield {
                    TabContainer(TabContainer.Props(props.anchor, props.img, props.tabId, props.style, props.onSelectedUrl, selectTab, props.tabId == s.tabId))
                  }
                )
              )
            ), //Only show collapse button here on desktops
              <.div(^.className := "col-sm-1",bss.hiddenXs)(
                coloredCollapseButton(s.linksHidden)
              )
          )
        ),

        <.div(^.borderRadius := "0px")(
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
            LinkList(links.items, s.linksHidden, p.searchFilter, p.linksType, p.podcastHandler)
          })
        ), if (!s.linksHidden &&dom.window.innerWidth < 768) panelBottomCollapse else <.span
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
  * Individual "tab" on one of the link container panels
  */
object TabContainer {
  case class AnchorProps(id: String = "", href: String = "", rel: String = "nofollow")
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
            //^.href:=p.anchor.href,
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
                    titleFilter: String = "",
                    linkType: LinkType,
                    podcastFn: (String) => Callback
                  )

  private val linkList = ReactComponentB[Props]("LinkList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: LinkObject) = {
        //Todo: finish implementing podcast player
        val podcastGlyph = if (p.linkType == LinkType.Podcast) {
          <.span(<.a(^.onClick --> p.podcastFn(item.podcastFile.get))(Icon.play), "  ")
        }
        else <.span()

        if (item.title.toLowerCase.contains(p.titleFilter.toLowerCase)) {
          <.li(bss.listGroup.item, ^.borderRadius := "0px")(
            podcastGlyph,
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
        <.ul(^.marginBottom := "0px", bss.listGroup.listGroup, ^.borderRadius := "0px")(
          !p.hidden ?= p.items map renderItem
        )
      }
      else {
        <.li(bss.listGroup.item, ^.borderRadius := "0px")("Couldn't pull any data")
      }
    })
    .build

  def apply(items: Seq[LinkObject], hidden: Boolean, searchFilter: String, linkType: LinkType, podcastFn: (String) => Callback) =
    linkList(Props(items, hidden, searchFilter, linkType, podcastFn))
}

object UniqueTarget {
  var counter: Int = 0
  def apply(): Int = {
    counter += 1
    counter
  }
}