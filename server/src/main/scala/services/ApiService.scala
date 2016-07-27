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
import spa.shared._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.NodeSeq
import play.api.libs.ws.{WS, WSClient}

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
        val name = if (feedbackData.name.trim.nonEmpty) feedbackData.name else feedbackData.email

        val email = Email(
          feedbackData.subject, {
            if (EmailValidation.isValid(feedbackData.email)) s"${name} <${feedbackData.email}>"
            else anonymousSender
          },
          Seq(recipient),
          bodyText = Some(feedbackData.message)
        )
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

@Singleton
class WebServiceParser @Inject()(wsClient: WSClient) {
  val siteMapping = Map(
    TabId.Reddit -> TabFeedSources.Reddit,
    TabId.RedditTop -> TabFeedSources.RedditTop,
    TabId.RedditTil -> TabFeedSources.RedditTil,
    TabId.AskReddit -> TabFeedSources.AskReddit,
    TabId.RedditVideos -> TabFeedSources.RedditVideos,

    TabId.RedditTechnology -> TabFeedSources.RedditTechnology,
    TabId.LifeHacker -> TabFeedSources.LifeHacker,
    TabId.Slashdot -> TabFeedSources.Slashdot,
    TabId.Techdirt -> TabFeedSources.Techdirt,

    TabId.RedditProgramming -> TabFeedSources.RedditProgramming,
    TabId.HackerNews -> TabFeedSources.HackerNews,
    TabId.RedditProgrammingTop -> TabFeedSources.RedditProgrammingTop,
    TabId.RedditCoding -> TabFeedSources.RedditCoding,
    TabId.RedditProgrammingHumor -> TabFeedSources.RedditProgrammingHumor,

    TabId.RedditPics -> TabFeedSources.RedditPics,
    TabId.RedditComics -> TabFeedSources.RedditComics,

    TabId.NoAgenda -> TabFeedSources.NoAgenda,
    TabId.HardcoreHistory -> TabFeedSources.HardcoreHistory,
    TabId.SecurityNow -> TabFeedSources.SecurityNow,
    TabId.CommonSense -> TabFeedSources.CommonSense


  )

  def refreshFeed(sourceId: String): Future[Seq[TitleLink]] = {
    if (sourceId.contains(TabId.Reddit)){
      parseRedditFeed(sourceId)
    }
    else {
      sourceId match {
        case TabId.LifeHacker => //println("Parsing lifehacker")
          parseRssV2(sourceId)
        case TabId.Slashdot => parseSlashdotFeed(sourceId)
        case TabId.Techdirt => parseRssV2(sourceId)

        case TabId.HackerNews => parseRssV2(sourceId)

        case TabId.NoAgenda => parseRssV2(sourceId)
        case TabId.HardcoreHistory => parseRssV2(sourceId)
        case TabId.SecurityNow => parseRssV2(sourceId)
        case TabId.CommonSense => parseRssV2(sourceId)
        case _ => println(s"Uh oh! from: $sourceId")
          Future(Seq.empty)
      }
    }

  }

  def parseSlashdotFeed(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)
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

  def parseRssV2(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)
    wsClient.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "channel" \\ "item")
        yield {
          val title = entry \\ "title"
          val href = entry \\ "link"
          TitleLink(title.text, href.text)
        }
      entries
    })
  }

  def parseRedditFeed(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)
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

case class TitleLink(title: String, href: String)