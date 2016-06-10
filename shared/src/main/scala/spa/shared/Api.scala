package spa.shared

import scala.concurrent.{Future}

trait Api {
  //Update a list of links
  def updateNewsList(tabId: String, contentType: String): Future[Seq[LinkObject]]
}
