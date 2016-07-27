package controllers

import java.nio.ByteBuffer
import javax.inject._

import actors.RootActor
import akka.actor.{ActorSystem, Props}
import boopickle.Default._
import models.{NewsLinkModel, NewsLinkStore}
import play.api.mvc._
import play.api.{Configuration, Environment}
import services.{ApiService, WebServiceParser}
import spa.shared.Api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

@Singleton
class Application @Inject() (
                  implicit val config: Configuration, env: Environment,
                  router: Router, apiService: ApiService, serviceParser: WebServiceParser) extends Controller {
  val systemName = "Scrapers"
  val system1 = ActorSystem(systemName)
  val rootActor = system1.actorOf(Props(classOf[RootActor], serviceParser, NewsLinkModel.store))

  def index = Action {
    Ok(views.html.index("The Internet (Abridged)"))
  }

  def autowireApi(path: String) = Action.async(parse.raw) {
    implicit request => try {
      println(s"Request path: $path")

      // get the request body as Array[Byte]
      val b = request.body.asBytes(parse.UNLIMITED).get

      // call Autowire route
      router.route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(b.asByteBuffer))
      ).map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        Ok(data)
      })
    } catch {
      case e: Exception =>
        Future(InternalServerError)
    }
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}
