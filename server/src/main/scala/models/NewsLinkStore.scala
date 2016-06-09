package models

import play.api.Play.current
import services.TitleLink

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import spa.shared._

/**
  * Created by molmsted on 4/4/2016.
  */

object NewsLinkModel {
  val store: NewsLinkStore = current.configuration.getString("module.links.store") match {
    case Some(impl) => impl match {
      case "Slick" => NewsLinkSlickStore
      case _ => NewsLinkSlickStore
    }
    case None => NewsLinkSlickStore
  }
}


trait NewsLinkStore {
  def all: Future[Seq[LinkObject]]

  def selectById(id: Long): Future[LinkObject]

  def selectByNewsSourceId(sourceId: String): Future[Seq[LinkObject]]

  def create(sourceId: String, tlinks: Seq[TitleLink]): Future[Seq[LinkObject]]

  //def createFromLinkRow(row: LinkRow): Future[LinkObject]

  def update(file: LinkObject): Future[Boolean]

  def delete(sourceId: String): Future[Boolean]
}

object NewsLinkSlickStore extends NewsLinkStore {

  import play.api.db.DB
  import slick.driver.H2Driver.api._

  class Links(tag: Tag) extends Table[LinkRow](tag, "LINKSTABLE"){
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def sourceId = column[String]("SOURCEID")
    def title = column[String]("TITLE")
    def href = column[String]("HREF")
    def * = (id, sourceId, title, href) <> (LinkRow.tupled, LinkRow.unapply)
  }

  private def db: Database = Database.forDataSource(DB.getDataSource())
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
          tlinks.map( item => LinkRow(None, sourceId.toString, item.title, item.href))
    }.map(_.map(_.toObj))
  }

  //override def createFromLinkRow(row: LinkRow): Future[LinkObject] =
  //  create(row.sourceId, row.title, row.href)


  def update(file: LinkObject): Future[Boolean]= {
    //TODO: implement update logic
    Future(true)
  }

  override def delete(sourceId: String): Future[Boolean]= {
    db.run(links.filter(_.sourceId === sourceId).delete).map(_ == 1)
  }
}

case class LinkRow(id: Option[Long], sourceId: String, title: String, href: String) {
  def toObj =
    LinkObject(this.id, this.sourceId, this.title, this.href)
}