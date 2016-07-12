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
  val podcasts = "podcasts"

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
    TabState(tech, TabId.RedditTechnology),
    TabState(images, TabId.RedditPics),
    TabState(programming, TabId.RedditProgramming),
    TabState(podcasts, TabId.NoAgenda)
  )

  def generateSubreddits = {
    val redditFrontpage = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", Reddit, icon = Icon.reddit, imgText = "Frontpage"),
      Reddit,
      onSelectedUrl = TabFeedSources.Reddit
    )

    val redditTop = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditTop, icon = Icon.reddit, imgText = "Top"),
      RedditTop,
      onSelectedUrl = TabFeedSources.RedditTop
    )

    val redditTil = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditTil, icon = Icon.reddit, imgText = "TIL"),
      RedditTil,
      onSelectedUrl = TabFeedSources.RedditTil
    )

    val askReddit = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", AskReddit, icon = Icon.reddit, imgText = "Ask"),
      AskReddit,
      onSelectedUrl = TabFeedSources.AskReddit
    )

    val redditVideos = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditVideos, icon = Icon.reddit, imgText = "Videos"),
      RedditVideos,
      onSelectedUrl = TabFeedSources.RedditVideos
    )

    List(
      redditFrontpage,
      redditTop,
      redditTil,
      askReddit,
      redditVideos
    )
  }

  def generateTech = {

    val redditTechnology = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditTechnology, icon = Icon.reddit, imgText = "Technology"),
      RedditTechnology,
      onSelectedUrl = TabFeedSources.RedditTechnology
    )

    val lifeHacker = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", LifeHacker, imgText = LifeHacker),
      LifeHacker,
      onSelectedUrl = TabFeedSources.LifeHacker
    )

    val slashdot = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", Slashdot, imgText = "/."),
      Slashdot,
      onSelectedUrl = TabFeedSources.Slashdot
    )

    val techdirt = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", Techdirt, imgText = "Techdirt"),
      Techdirt,
      onSelectedUrl = TabFeedSources.Techdirt
    )

    List(
      redditTechnology,
      lifeHacker,
      slashdot,
      techdirt
    )
  }

  def generateProgramming = {
    val redditProgramming = ContainerTab.Props(
      ContainerTab.AnchorProps(),
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

    val redditProgrammingTop = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditProgrammingTop, icon = Icon.reddit, imgText = "Top"),
      RedditProgrammingTop,
      onSelectedUrl = TabFeedSources.RedditProgrammingTop
    )

    val redditCoding = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditCoding, icon = Icon.reddit, imgText = "Coding"),
      RedditCoding,
      onSelectedUrl = TabFeedSources.RedditCoding
    )

    val redditProgHumor = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditProgrammingHumor, imgText = "Humor"),
      RedditProgrammingHumor,
      onSelectedUrl = TabFeedSources.RedditProgrammingHumor
    )

    List(
      redditProgramming,
      hackerNews,
      redditProgrammingTop,
      redditCoding,
      redditProgHumor
    )
  }

  def generateImages = {
    val redditPics = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditPics, icon = Icon.reddit, imgText = "Pics"),
      RedditPics,
      onSelectedUrl = TabFeedSources.RedditPics
    )

    val redditComics = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", RedditComics, icon = Icon.reddit, imgText = "Comics"),
      RedditComics,
      onSelectedUrl = TabFeedSources.RedditComics
    )

    List(
      redditPics,
      redditComics
    )
  }

  def generatePodcasts = {
    val noAgenda = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", NoAgenda, imgText = "No Agenda"),
      NoAgenda,
      onSelectedUrl = TabFeedSources.NoAgenda
    )

    val hardcoreHistory = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", HardcoreHistory, imgText = "Hardcore History"),
      HardcoreHistory,
      onSelectedUrl = TabFeedSources.HardcoreHistory
    )

    val securityNow = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", SecurityNow, imgText = "Security Now"),
      SecurityNow,
      onSelectedUrl = TabFeedSources.SecurityNow
    )

    val commonSense = ContainerTab.Props(
      ContainerTab.AnchorProps(),
      ContainerTab.ImageProps("src", CommonSense, imgText = "Common Sense"),
      CommonSense,
      onSelectedUrl = TabFeedSources.CommonSense
    )

    List(
      noAgenda,
      hardcoreHistory,
      securityNow,
      commonSense
    )
  }
}
