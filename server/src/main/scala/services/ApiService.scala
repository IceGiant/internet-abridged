package services

import java.util.{Date, UUID}
import javax.inject.Inject

import com.google.inject.Singleton
import com.ning.http.client.Response
import models.{NewsLinkModel, NewsLinkStore}
import play.api.Configuration
import play.api.Play.current
import play.api.libs.mailer.{Email, MailerClient}
import play.libs.F.Promise
import play.api.data.validation
import spa.shared.LinkType.LinkType
import spa.shared._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.NodeSeq
import play.api.libs.ws.{WS, WSClient}

import scalaz._
import Scalaz._

class ApiService @Inject() (implicit val config: Configuration,
                            mailer: MailerClient, newsStore: NewsLinkModel) extends Api {

  override def updateNewsList(tabId: String, contentType: String): Future[Seq[LinkObject]] = {
    //Make a call to the DB and get the list of links for the relevant news source
    newsStore.store.selectByNewsSourceId(tabId)
  }

  //Get email to send feedback to from the config
  val recipient: String = config.getString("play.mailer.user") match {
    case Some(user) => user
    case _ => println("Missing mailer configuration")
      ""
  }

  val anonymousSender = "Anonymous <anon@null.null>"


  override def submitFeedback(feedbackData: EmailFormData): Future[Boolean] = {
    //Send information off via Play mailer to my email address
    try {
      if (feedbackData.message.trim.length > 0 && feedbackData.subject.trim.length > 0) {
        val name = if (feedbackData.name.trim.nonEmpty) {
          feedbackData.name
        } else {
          feedbackData.email
        }

        val address = if (EmailValidation.isValid(feedbackData.email)) {
          s"${name} <${feedbackData.email}>"
        }
        else {
          anonymousSender
        }

        val email = Email(
          feedbackData.subject,
          address,
          Seq(recipient),
          bodyText = Some(feedbackData.message),
          replyTo = Some(address)
        )
        println(email.toString)
        Future(mailer.send(email)).map(_ match {
          case s: String => println(s"Got email sending result: $s")
            true
          case _ => println("Email sending error")
            false
        })
      } else Future(false)
    }
    catch {
      case e: Exception => println(feedbackData.toString)
        e.printStackTrace()
        Future(false)
    }
  }
}

case class FeedDetails(url: String, uiString: String, linkType: LinkType)

@Singleton
class WebServiceParser @Inject()(wsClient: WSClient) {
  val redditMap = (Feeds.redditMap.map(entry => entry.head) zip
    Feeds.redditMap.map(entry => (entry(1), entry.last, LinkType.Article.toString))).toMap
  val techMap = (Feeds.techMap.map(entry => entry.head) zip
    Feeds.techMap.map(entry => (entry(1), entry.last, LinkType.Article.toString))).toMap
  val programmingMap = (Feeds.programmingMap.map(entry => entry.head) zip
    Feeds.programmingMap.map(entry => (entry(1), entry.last, LinkType.Article.toString))).toMap
  val comicsMap = (Feeds.comicsMap.map(entry => entry.head) zip
    Feeds.comicsMap.map(entry => (entry(1), entry.last, LinkType.Article.toString))).toMap
  val securityMap = (Feeds.securityMap.map(entry => entry.head) zip
    Feeds.securityMap.map(entry => (entry(1), entry.last, LinkType.Article.toString))).toMap
  val podcastMap = (Feeds.podcastMap.map(entry => entry.head) zip
    Feeds.podcastMap.map(entry => (entry(1), entry.last, LinkType.Podcast.toString))).toMap

  val siteMapping = redditMap |+|
                    techMap |+|
                    comicsMap |+|
                    programmingMap |+|
                    securityMap |+|
                    podcastMap

  def refreshFeed(sourceId: String): Future[Seq[TitleLink]] = {
    if (sourceId.contains(FeedIds.Reddit)){
      parseRedditFeed(sourceId)
    }
    else {
      sourceId match {
        case FeedIds.LifeHacker => //println("Parsing lifehacker")
          parseRssV2(sourceId)
        case FeedIds.Slashdot => parseSlashdotFeed(sourceId)
        case FeedIds.Techdirt => parseRssV2(sourceId)
        case FeedIds.ArsTechnica => parseRssV2(sourceId)

        case FeedIds.Xkcd => parseRssV2(sourceId)
        case FeedIds.Dilbert => parseFeedburnerXml(sourceId)
        case FeedIds.CyanideHappiness => parseRssV2(sourceId)
        case FeedIds.GirlGenius => parseRssV2(sourceId)
        case FeedIds.LookingForGroup => parseFeedburnerXml(sourceId)

        case FeedIds.HackerNews => parseRssV2(sourceId)

        case FeedIds.DarkReading => parseRssV2(sourceId)
        case FeedIds.Schneier => parseRedditFeed(sourceId)
        case FeedIds.Krebs => parseRssV2(sourceId)

        case FeedIds.NoAgenda => parseRssV2(sourceId, LinkType.Podcast)
        case FeedIds.HardcoreHistory => parseRssV2(sourceId, LinkType.Podcast)
        case FeedIds.SecurityNow => parseRssV2(sourceId, LinkType.Podcast)
        case FeedIds.CommonSense => parseRssV2(sourceId, LinkType.Podcast)
        case _ => println(s"Uh oh! from: $sourceId")
          Future(Seq.empty)
      }
    }

  }

  def parseSlashdotFeed(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)._1
    wsClient.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "item")
        yield {
          val title = entry \\ "title"
          val href = entry \\ "link"
          TitleLink(title.text, href.text)
        }
      entries
    })
  }

  def parseRssV2(sourceId: String, linkType: LinkType = LinkType.Article): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)._1
    wsClient.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "channel" \\ "item")
        yield {
          val title = entry \\ "title"
          val href = entry \\ "link"
          if (linkType == LinkType.Podcast) {
            val mp3Link = entry \\ "enclosure" \ "@url"
            TitleLink(title.text, href.text, Some(mp3Link.text))
          }
          else TitleLink(title.text, href.text)
        }
      entries
    })
  }

  def parseFeedburnerXml(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)._1
    wsClient.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "feed" \\ "entry")
        yield {
          val title = entry \\ "title"
          val link = entry \\ "link" \ "@href"
          TitleLink(title.text, link.text)
        }
      entries
    })
  }

  def parseRedditFeed(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)._1
    wsClient.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "feed" \\ "entry")
        yield {
          val title = entry \\ "title"
          val redditLink = entry \\ "link"
          val source = entry \\ "content"
          val html = source.text
          val end = html.split("""<span><a href="""")
          val href = end(1).replace("&amp;", "&").split("\">")
          TitleLink(title.text, href.head)
        }
      entries
    })
  }
}

case class TitleLink(title: String, href: String, podcastFile: Option[String] = None)