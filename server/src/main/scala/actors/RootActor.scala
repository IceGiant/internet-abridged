package actors

import akka.actor.SupervisorStrategy.Restart
import akka.remote.ContainerFormats.ActorRef
import akka.actor._
import spa.shared.TabId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by molmsted on 5/23/2016.
  */
object RootActor {
  def props = Props(new RootActor)
}

class RootActor extends Actor {
  //implicit val node = Cluster(ScraperCluster.system1)

  //ScraperCluster.system1.eventStream.subscribe(self, classOf[DbUpdateComplete])
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case e: Exception => println("Exception detected by root")
      Restart
  }
  //initialize DB actors
  println("try to creat actor")
  val reddit = context.system.actorOf(Props(classOf[SupervisorActor], TabId.Reddit))
  val redditTop = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditTop))
  val redditTil = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditTil))
  val askReddit = context.system.actorOf(Props(classOf[SupervisorActor], TabId.AskReddit))
  val redditVideos = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditVideos))

  val redditTech = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditTech))
  val lifeHacker = context.system.actorOf(Props(classOf[SupervisorActor], TabId.LifeHacker))
  val redditTechnology = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditTechnology))

  val redditProgramming = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditProgramming))
  val hackerNews = context.system.actorOf(Props(classOf[SupervisorActor], TabId.HackerNews))
  val redditProgrammingTop = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditProgrammingTop))
  val redditCoding = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditCoding))
  val redditProgrammingHumor = context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditProgrammingHumor))

  val redditPics= context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditPics))
  val redditComics= context.system.actorOf(Props(classOf[SupervisorActor], TabId.RedditComics))

  val noAgenda = context.system.actorOf(Props(classOf[SupervisorActor], TabId.NoAgenda))
  val hardcoreHistory = context.system.actorOf(Props(classOf[SupervisorActor], TabId.HardcoreHistory))
  val securityNow = context.system.actorOf(Props(classOf[SupervisorActor], TabId.SecurityNow))
  val commonSense = context.system.actorOf(Props(classOf[SupervisorActor], TabId.CommonSense))

  println("root actor init")

  def receive = {
    case Startup => println("Started")
    case _ => println("test")
  }
}

case object Startup
