package models

import javax.inject._
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.api.Play.current
import services.TitleLink
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import spa.shared._


class NewsLinkModel @Inject() (implicit val config: Configuration, newsStore: NewsLinkSlickStore) {
  val store: NewsLinkStore = config.getString("module.links.store") match {
    case Some(impl) => impl match {
      case "Slick" => newsStore
      case _ => newsStore
    }
    case None => newsStore
  }
}


trait NewsLinkStore  {
  def all: Future[Seq[LinkObject]]

  def selectById(id: Long): Future[LinkObject]

  def selectByNewsSourceId(sourceId: String): Future[Seq[LinkObject]]

  def create(sourceId: String, tlinks: Seq[TitleLink]): Future[Seq[LinkObject]]

  //def createFromLinkRow(row: LinkRow): Future[LinkObject]

  def update(file: LinkObject): Future[Boolean]

  def delete(sourceId: String): Future[Boolean]
}

@Singleton()
class NewsLinkSlickStore @Inject() (dbConfigProvider: DatabaseConfigProvider) extends NewsLinkStore {

  import slick.driver.H2Driver.api._

  class Links(tag: Tag) extends Table[LinkRow](tag, "LINKSTABLE"){
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def sourceId = column[String]("SOURCEID")
    def title = column[String]("TITLE")
    def href = column[String]("HREF")
    def podcastFile = column[Option[String]]("PODCASTFILE")
    def * = (id, sourceId, title, href, podcastFile) <> (LinkRow.tupled, LinkRow.unapply)
  }

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private def db: Database = dbConfig.db
  val links = TableQuery[Links]

  override def all: Future[Seq[LinkObject]] = {
    db.run(links.sortBy(_.id.asc).map(f=>f).result.map(_.map(_.toObj)))
  }

  override def selectById(id: Long): Future[LinkObject] = {
    db.run(links.filter(f => f.id === id).map(f=>f).result.map(_.head.toObj))
  }

  override def selectByNewsSourceId(sourceId: String): Future[Seq[LinkObject]] = {
    db.run(links.filter(f => f.sourceId === sourceId.toString).map(f=>f).result.map(_.map(_.toObj)))
  }

  override def create(sourceId: String, tlinks: Seq[TitleLink]): Future[Seq[LinkObject]] = {
    db.run{
        links returning links.map(_.id) into ((link, id) => link.copy(id = id)) ++=
          tlinks.map( item => LinkRow(None, sourceId.toString, item.title, item.href, item.podcastFile))
    }.map(_.map(_.toObj))
  }

  def update(file: LinkObject): Future[Boolean]= {
    //TODO: implement update logic
    Future(true)
  }

  override def delete(sourceId: String): Future[Boolean]= {
    db.run(links.filter(_.sourceId === sourceId).delete).map(_ == 1)
  }
}

case class LinkRow(id: Option[Long], sourceId: String, title: String, href: String, podcastFile: Option[String]) {
  def toObj =
    LinkObject(this.id, this.sourceId, this.title, this.href, this.podcastFile)
}