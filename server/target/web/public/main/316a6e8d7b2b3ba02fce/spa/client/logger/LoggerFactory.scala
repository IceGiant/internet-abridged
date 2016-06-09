package spa.client.logger

import scala.annotation.elidable
import scala.annotation.elidable._

trait Logger {
  /*
   * Use @elidable annotation to completely exclude functions from the compiler generated byte-code based on
   * the specified level. In a production build most logging functions will simply disappear with no runtime
   * performance penalty.
   *
   * Specify level as a compiler parameter
   * > scalac -Xelide-below INFO
  */
  @elidable(FINEST) def trace(msg: String, e: Exception): Unit
  @elidable(FINEST) def trace(msg: String): Unit
  @elidable(FINE) def debug(msg: String, e: Exception): Unit
  @elidable(FINE) def debug(msg: String): Unit
  @elidable(INFO) def info(msg: String, e: Exception): Unit
  @elidable(INFO) def info(msg: String): Unit
  @elidable(WARNING) def warn(msg: String, e: Exception): Unit
  @elidable(WARNING) def warn(msg: String): Unit
  @elidable(SEVERE) def error(msg: String, e: Exception): Unit
  @elidable(SEVERE) def error(msg: String): Unit
  @elidable(SEVERE) def fatal(msg: String, e: Exception): Unit
  @elidable(SEVERE) def fatal(msg: String): Unit

  def enableServerLogging(url: String): Unit
  def disableServerLogging(): Unit
}

object LoggerFactory {
  private[logger] def createLogger(name: String) = {}

  lazy val consoleAppender = new BrowserConsoleAppender
  lazy val popupAppender = new PopUpAppender

  /**
   * Create a logger that outputs to browser console
   */
  def getLogger(name: String): Logger = {
    val nativeLogger = Log4JavaScript.log4javascript.getLogger(name)
    nativeLogger.addAppender(consoleAppender)
    new L4JSLogger(nativeLogger)
  }

  /**
   * Create a logger that outputs to a separate popup window
   */
  def getPopUpLogger(name: String): Logger = {
    val nativeLogger = Log4JavaScript.log4javascript.getLogger(name)
    nativeLogger.addAppender(popupAppender)
    new L4JSLogger(nativeLogger)
  }
}
