package spa.client.modules

import spa.client.components.Icon
import spa.client.logger._
import spa.shared.{TabFeedSources, TabId}
import spa.shared.TabId._

/**
  * Created by skypage on 6/8/16.
  */
object HomeInits {
  val reddit = "reddit"
  val tech = "tech"
  val images = "images"
  val programming = "programming"

  case class TabState(id: String, selectedTab: String)

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
    TabState(reddit, TabId.Reddit),
    TabState(tech, TabId.RedditTech),
    TabState(images, TabId.RedditPics),
    TabState(programming, TabId.RedditProgramming)
  )

  def generateSubreddits = {
    val redditFrontpage = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", Reddit, icon = Icon.reddit, imgText = "Frontpage"),
      Reddit,
      onSelectedUrl = TabFeedSources.Reddit
    )

    val redditTop = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditTop, icon = Icon.reddit, imgText = "Top"),
      RedditTop,
      onSelectedUrl = TabFeedSources.RedditTop
    )

    val redditTil = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditTil, icon = Icon.reddit, imgText = "TIL"),
      RedditTil,
      onSelectedUrl = TabFeedSources.RedditTil
    )

    val askReddit = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", AskReddit, icon = Icon.reddit, imgText = "Ask"),
      AskReddit,
      onSelectedUrl = TabFeedSources.AskReddit
    )

    List(
      redditFrontpage,
      redditTop,
      redditTil,
      askReddit
    )
  }

  def generateTech = {
    val redditTech = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditTech, icon = Icon.reddit, imgText = "Tech News"),
      RedditTech,
      onSelectedUrl = TabFeedSources.RedditTech
    )

    val lifeHacker = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", LifeHacker, imgText = LifeHacker),
      LifeHacker,
      onSelectedUrl = TabFeedSources.LifeHacker
    )

    val redditTechnology = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditTechnology, icon = Icon.reddit, imgText = "Technology"),
      RedditTechnology,
      onSelectedUrl = TabFeedSources.RedditTechnology
    )

    List(
      redditTech,
      lifeHacker,
      redditTechnology
    )
  }

  def generateProgramming = {
    val redditProgramming = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditProgramming, icon = Icon.reddit, imgText = "Programming"),
      RedditProgramming,
      onSelectedUrl = TabFeedSources.RedditProgramming
    )

    val hackerNews = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", HackerNews, icon = Icon.yCombinator, imgText = "Hacker News"),
      HackerNews,
      onSelectedUrl = TabFeedSources.HackerNews
    )

    val redditCoding = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditCoding, icon = Icon.reddit, imgText = "Coding"),
      RedditCoding,
      onSelectedUrl = TabFeedSources.RedditCoding
    )

    val redditProgHumor = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditProgrammingHumor, imgText = "Humor"),
      RedditProgrammingHumor,
      onSelectedUrl = TabFeedSources.RedditProgrammingHumor
    )

    List(
      redditProgramming,
      hackerNews,
      redditCoding,
      redditProgHumor
    )
  }

  def generateImages = {
    val redditPics = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditPics, icon = Icon.reddit, imgText = "Pics"),
      RedditPics,
      onSelectedUrl = TabFeedSources.RedditPics
    )

    val redditComics = ContainerTab.Props(
      ContainerTab.AnchorProps("ns"),
      ContainerTab.ImageProps("src", RedditComics, icon = Icon.reddit, imgText = "Comics"),
      RedditComics,
      onSelectedUrl = TabFeedSources.RedditComics
    )

    List(
      redditPics,
      redditComics
    )
  }
}
