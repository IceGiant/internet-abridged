package spa.client.modules

import spa.client.components.Icon
import spa.client.logger._
import spa.shared.{FeedUrls, FeedIds}
import spa.shared.FeedIds._

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
    TabState(reddit, FeedIds.Reddit),
    TabState(tech, FeedIds.RedditTechnology),
    TabState(images, FeedIds.RedditPics),
    TabState(programming, FeedIds.RedditProgramming),
    TabState(podcasts, FeedIds.NoAgenda)
  )

  def generateSubreddits = {
    val redditFrontpage = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", Reddit, icon = Icon.reddit, imgText = "Frontpage"),
      Reddit,
      onSelectedUrl = FeedUrls.Reddit
    )

    val redditTop = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditTop, icon = Icon.reddit, imgText = "Top"),
      RedditTop,
      onSelectedUrl = FeedUrls.RedditTop
    )

    val redditTil = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditTil, icon = Icon.reddit, imgText = "TIL"),
      RedditTil,
      onSelectedUrl = FeedUrls.RedditTil
    )

    val askReddit = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", AskReddit, icon = Icon.reddit, imgText = "Ask"),
      AskReddit,
      onSelectedUrl = FeedUrls.AskReddit
    )

    val redditVideos = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditVideos, icon = Icon.reddit, imgText = "Videos"),
      RedditVideos,
      onSelectedUrl = FeedUrls.RedditVideos
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

    val redditTechnology = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditTechnology, icon = Icon.reddit, imgText = "Technology"),
      RedditTechnology,
      onSelectedUrl = FeedUrls.RedditTechnology
    )

    val lifeHacker = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", LifeHacker, imgText = LifeHacker),
      LifeHacker,
      onSelectedUrl = FeedUrls.LifeHacker
    )

    val slashdot = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", Slashdot, imgText = "/."),
      Slashdot,
      onSelectedUrl = FeedUrls.Slashdot
    )

    val techdirt = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", Techdirt, imgText = "Techdirt"),
      Techdirt,
      onSelectedUrl = FeedUrls.Techdirt
    )

    val ars =

    List(
      redditTechnology,
      lifeHacker,
      slashdot,
      techdirt
    )
  }

  def generateProgramming = {
    val redditProgramming = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditProgramming, icon = Icon.reddit, imgText = "Programming"),
      RedditProgramming,
      onSelectedUrl = FeedUrls.RedditProgramming
    )

    val hackerNews = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", HackerNews, icon = Icon.yCombinator, imgText = "Hacker News"),
      HackerNews,
      onSelectedUrl = FeedUrls.HackerNews
    )

    val redditProgrammingTop = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditProgrammingTop, icon = Icon.reddit, imgText = "Top"),
      RedditProgrammingTop,
      onSelectedUrl = FeedUrls.RedditProgrammingTop
    )

    val redditCoding = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditCoding, icon = Icon.reddit, imgText = "Coding"),
      RedditCoding,
      onSelectedUrl = FeedUrls.RedditCoding
    )

    val redditProgHumor = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditProgrammingHumor, imgText = "Humor"),
      RedditProgrammingHumor,
      onSelectedUrl = FeedUrls.RedditProgrammingHumor
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
    val redditPics = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditPics, icon = Icon.reddit, imgText = "Pics"),
      RedditPics,
      onSelectedUrl = FeedUrls.RedditPics
    )

    val redditComics = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", RedditComics, icon = Icon.reddit, imgText = "Comics"),
      RedditComics,
      onSelectedUrl = FeedUrls.RedditComics
    )

    List(
      redditPics,
      redditComics
    )
  }

  def generatePodcasts = {
    val noAgenda = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", NoAgenda, imgText = "No Agenda"),
      NoAgenda,
      onSelectedUrl = FeedUrls.NoAgenda
    )

    val hardcoreHistory = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", HardcoreHistory, imgText = "Hardcore History"),
      HardcoreHistory,
      onSelectedUrl = FeedUrls.HardcoreHistory
    )

    val securityNow = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", SecurityNow, imgText = "Security Now"),
      SecurityNow,
      onSelectedUrl = FeedUrls.SecurityNow
    )

    val commonSense = TabContainer.Props(
      TabContainer.AnchorProps(),
      TabContainer.ImageProps("src", CommonSense, imgText = "Common Sense"),
      CommonSense,
      onSelectedUrl = FeedUrls.CommonSense
    )

    List(
      noAgenda,
      hardcoreHistory,
      securityNow,
      commonSense
    )
  }
}
