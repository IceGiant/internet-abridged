package services

import java.util.{Date, UUID}

import com.ning.http.client.Response
import play.api.Play.current
import play.libs.F.Promise
import spa.shared._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.NodeSeq
import play.api.libs.ws.WS

class ApiService extends Api {
  /*
  var todos = Seq(
    TodoItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", TodoLow, false),
    TodoItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", TodoNormal, false),
    TodoItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", TodoHigh, false),
    TodoItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", TodoNormal, true)
  )

  val siteMapping = Map(
    TabId.Reddit -> TabFeedSources.Reddit,
    TabId.RedditTop -> TabFeedSources.RedditTop
  )

  override def welcome(name: String): String = s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }*/

  override def updateNewsList(tabId: String, contentType: String): Future[Seq[LinkObject]] = {
    //Make a call to the DB and get the list of links for the relevant news source
    /*try {
      Await.result(*/models.NewsLinkModel.store.selectByNewsSourceId(tabId)/*, Duration.Inf)
    }
    catch {
      case e: Exception =>
        println("caught exception")
        println(e.getMessage)
        Seq.empty[LinkObject]
    }*/
  }

}


object ApiService {
  val siteMapping = Map(
    TabId.Reddit -> TabFeedSources.Reddit,
    TabId.RedditTop -> TabFeedSources.RedditTop,
    TabId.RedditTil -> TabFeedSources.RedditTil,
    TabId.AskReddit -> TabFeedSources.AskReddit,

    TabId.RedditTech -> TabFeedSources.RedditTech,
    TabId.RedditTechnology -> TabFeedSources.RedditTechnology,

    TabId.RedditProgramming -> TabFeedSources.RedditProgramming,
    TabId.RedditCoding -> TabFeedSources.RedditCoding,
    TabId.RedditProgrammingHumor -> TabFeedSources.RedditProgrammingHumor,

    TabId.RedditPics -> TabFeedSources.RedditPics,
    TabId.RedditComics -> TabFeedSources.RedditComics
  )

  def refreshFeed(sourceId: String): Future[Seq[TitleLink]] = {
    if (sourceId.contains(TabId.Reddit)){
      parseRedditFeed(sourceId)
    }
    else {
      println(s"Uh oh! from: $sourceId")
      Future(Seq.empty)
    }
  }

  def parseRedditFeed(sourceId: String): Future[Seq[TitleLink]] = {
    val url = siteMapping(sourceId)
    WS.url(url).get().map( futResponse => {
      val entries = for (entry <- futResponse.xml \\ "feed" \\ "entry")
        yield {
          val title = entry \\ "title"
          val redditLink = entry \\ "link"
          val source = entry \\ "content"
          val html = source.text
          val end = html.split("""<span><a href="""")
          val href = end(1).split("\">")
          TitleLink(title.text, href.head)
        }
      entries
    })
  }
}

case class TitleLink(title: String, href: String)