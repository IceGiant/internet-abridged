package spa.shared

import scala.concurrent.{Future}

trait Api {
  // message of the day
  def welcome(name: String): String

  // get Todo items
  def getTodos(): Seq[TodoItem]

  // update a Todo
  def updateTodo(item: TodoItem): Seq[TodoItem]

  // delete a Todo
  def deleteTodo(itemId: String): Seq[TodoItem]

  //Update a list of links
  def updateNewsList(tabId: String, contentType: String): Future[Seq[LinkObject]]
}
