package spa.shared

import boopickle.Default._

sealed trait TodoPriority

case object TodoLow extends TodoPriority

case object TodoNormal extends TodoPriority

case object TodoHigh extends TodoPriority

case class TodoItem(id: String, timeStamp: Int, content: String, priority: TodoPriority, completed: Boolean)

object TodoPriority {
  implicit val todoPriorityPickler: Pickler[TodoPriority] = generatePickler[TodoPriority]
}


case class LinkObject(id: Option[Long], sourceId: String, title: String, href: String)

case class SourceInfo(url: String, text: String)

object Feeds {
  val redditMap = List(
    List(FeedIds.Reddit, FeedUrls.Reddit, "Frontpage"),
    List(FeedIds.RedditTop, FeedUrls.RedditTop, "Top"),
    List(FeedIds.RedditTil, FeedUrls.RedditTil, "TIL"),
    List(FeedIds.RedditPics, FeedUrls.RedditPics, "Pics"),
    List(FeedIds.AskReddit, FeedUrls.AskReddit, "Ask"),
    List(FeedIds.RedditVideos, FeedUrls.RedditVideos, "Videos")
  )

  val techMap = List(
    List(FeedIds.RedditTechnology, FeedUrls.RedditTechnology, "Technology"),
    List(FeedIds.LifeHacker, FeedUrls.LifeHacker, FeedIds.LifeHacker),
    List(FeedIds.Slashdot, FeedUrls.Slashdot, "/."),
    List(FeedIds.Techdirt, FeedUrls.Techdirt, "Techdirt"),
    List(FeedIds.ArsTechnica, FeedUrls.ArsTechnica, "Ars")
  )

  val programmingMap = List(
    List(FeedIds.RedditProgramming, FeedUrls.RedditProgramming, "Programming"),
    List(FeedIds.HackerNews, FeedUrls.HackerNews, "Hacker News"),
    List(FeedIds.RedditProgrammingTop, FeedUrls.RedditProgrammingTop, "Top"),
    List(FeedIds.RedditCoding, FeedUrls.RedditCoding, "Coding"),
    List(FeedIds.RedditProgrammingHumor, FeedUrls.RedditProgrammingHumor,  "Humor")
  )

  val comicsMap = List(
    List(FeedIds.RedditComics, FeedUrls.RedditComics, "Comics"),
    List(FeedIds.Xkcd, FeedUrls.Xkcd, "xkcd"),
    List(FeedIds.Dilbert, FeedUrls.Dilbert, "Dilbert"),
    List(FeedIds.CyanideHappiness, FeedUrls.CyanideHappiness, "C&H"),
    List(FeedIds.GirlGenius, FeedUrls.GirlGenius, "Girl Genius"),
    List(FeedIds.LookingForGroup, FeedUrls.LookingForGroup, "LFG")
  )

  val podcastMap = List(
    List(FeedIds.NoAgenda, FeedUrls.NoAgenda, "No Agenda"),
    List(FeedIds.HardcoreHistory, FeedUrls.HardcoreHistory, "Hardcore History"),
    List(FeedIds.SecurityNow, FeedUrls.SecurityNow, "Security Now"),
    List(FeedIds.CommonSense, FeedUrls.CommonSense, "Common Sense")
  )
}

object FeedIds{
  val Reddit = "Reddit"
  val RedditTop = "RedditTop"
  val RedditTil = "RedditTil"
  val RedditPics = "RedditPics"
  val AskReddit = "AskReddit"
  val RedditVideos = "RedditVideos"

  val RedditTechnology = "RedditTechnology"
  val LifeHacker = "LifeHacker"
  val Slashdot = "Slashdot"
  val Techdirt = "Techdirt"
  val ArsTechnica = "ArsTechnica"

  val RedditProgramming = "RedditProgramming"
  val HackerNews = "HackerNews"
  val RedditProgrammingTop = "RedditProgrammingTop"
  val RedditCoding = "RedditCoding"
  val RedditProgrammingHumor = "RedditProgrammingHumor"

  val RedditComics = "RedditComics"
  val Xkcd = "Xkcd"
  val Dilbert = "Dilbert"
  val CyanideHappiness = "CyanideHappiness"
  val GirlGenius = "GirlGenius"
  val LookingForGroup = "LookingForGroup"

  val ScottAdams = "ScottAdams"

  val NoAgenda = "NoAgenda"
  val HardcoreHistory = "HardcoreHistory"
  val SecurityNow = "SecurityNow"
  val CommonSense = "CommonSense"


  /*Maybe
  val Digg = "Digg"
  val GoogleNews = "GoogleNews"
  val GoogleNewsUS = "GoogleNewsUS"
  */
}




object FeedUrls {
  val Reddit = "https://reddit.com/.rss"
  val RedditTop = "https://reddit.com/top/.rss"
  val RedditTil = "https://www.reddit.com/r/todayilearned/.rss"
  val RedditPics = "https://www.reddit.com/r/pics/.rss"
  val AskReddit = "https://www.reddit.com/r/AskReddit/.rss"
  val RedditVideos = "https://www.reddit.com/r/videos/.rss"

  val RedditTechnology = "https://www.reddit.com/r/technology/.rss"
  val LifeHacker = "http://lifehacker.com/rss"
  val Slashdot = "http://rss.slashdot.org/Slashdot/slashdot"
  val Techdirt = "https://feeds.feedburner.com/techdirt/feed"
  val ArsTechnica = "http://feeds.arstechnica.com/arstechnica/index"

  val RedditProgramming = "https://www.reddit.com/r/programming/.rss"
  val HackerNews = "https://news.ycombinator.com/rss"
  val RedditProgrammingTop = "https://www.reddit.com/r/programming/.rss"
  val RedditCoding = "https://www.reddit.com/r/coding/.rss"
  val RedditProgrammingHumor = "https://www.reddit.com/r/ProgrammerHumor/.rss"

  val RedditComics = "https://www.reddit.com/r/comics/.rss"
  val Xkcd = "https://www.xkcd.com/rss.xml"
  val Dilbert = "http://dilbert.com/feed"
  val CyanideHappiness = "http://explosm.net/rss.php"
  val GirlGenius = "http://www.girlgeniusonline.com/ggmain.rss"
  val LookingForGroup = "https://feeds.feedburner.com/LookingForGroup"

  val ScottAdams = "http://feed.dilbert.com/dilbert/blog"

  val NoAgenda = "http://feed.nashownotes.com/rss.xml"
  val HardcoreHistory = "https://feeds.feedburner.com/dancarlin/history?format=xml"
  val SecurityNow = "http://feeds.twit.tv/sn.xml"
  val CommonSense = "https://feeds.feedburner.com/dancarlin/commonsense?format=xml"



  /* maybe
  val Digg = "https://digg.com"
  val GoogleNews = "https://news.google.com"
  val GoogleNewsUS = "https://news.google.com/news/section?pz=1&cf=all&topic=n&ict=ln"
  */
}

object EmailValidation {
  //Below regex based on W3C recommendation here: https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def isValid(e: String): Boolean = e match{
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }
}

case class EmailFormData(name: String = "Anonymous",
                         email: String = "anonymous@internet-abridged.com",
                         subject: String, message: String)