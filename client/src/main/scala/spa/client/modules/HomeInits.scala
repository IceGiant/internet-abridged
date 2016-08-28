package spa.client.modules

import japgolly.scalajs.react.ReactNode
import spa.client.components.Icon
import spa.client.logger._
import spa.shared.{FeedUrls, FeedIds, Feeds}
import spa.shared.FeedIds._
import japgolly.scalajs.react.vdom.prefix_<^._


object HomeInits {
  val reddit = "reddit"
  val tech = "tech"
  val comics = "comics"
  val programming = "programming"
  val security = "security"
  val podcasts = "podcasts"

  case class TabState(id: String, selectedTab: String)

  //Todo: actually choose whether to implement in-page filtering, and finish or remove accordingly
  def updateCache(item: TabState): Unit = {
    if(cachedTabStates.exists(_.id == item.id)) {
      cachedTabStates = cachedTabStates.collect {
        case i if i.id == item.id =>
          log.debug(s"item ${item.id} ")
          item
        case i => i
      }
      log.debug(s"Saved state ${item.selectedTab}")

    }
  }

  def getState(sectionId: String): String = {
    //log.debug(s"Got state as ${cachedTabStates.filter(_.id == sectionId).head.selectedTab}")
    cachedTabStates.filter(_.id == sectionId).head.selectedTab
  }

  var cachedTabStates = Seq(
    TabState(reddit, FeedIds.Reddit),
    TabState(tech, FeedIds.RedditTechnology),
    TabState(comics, FeedIds.RedditComics),
    TabState(programming, FeedIds.RedditProgramming),
    TabState(security, FeedIds.Schneier),
    TabState(podcasts, FeedIds.NoAgenda)
  )

  def generateSubreddits = {
    Feeds.redditMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, icon = selectIcon(entry.head), imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def generateTech = {
    Feeds.techMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, icon = selectIcon(entry.head), imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def generateProgramming = {
    Feeds.programmingMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, icon = selectIcon(entry.head), imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def generateComics = {
    Feeds.comicsMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, icon = selectIcon(entry.head), imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def generateSecurity = {
    Feeds.securityMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, icon = selectIcon(entry.head), imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def generatePodcasts = {
    Feeds.podcastMap.map(entry =>
      TabContainer.Props(
        TabContainer.AnchorProps(),
        TabContainer.ImageProps("src", entry.head, imgText = entry.last),
        entry.head,
        onSelectedUrl = entry(1))
    )
  }

  def selectIcon(id: String): ReactNode = {
    if (id == FeedIds.RedditProgrammingHumor)
      <.span()
    else if (id.contains(FeedIds.Reddit))
      Icon.reddit
    else if (id == FeedIds.HackerNews)
      Icon.yCombinator
    else
      <.span()
  }

}
