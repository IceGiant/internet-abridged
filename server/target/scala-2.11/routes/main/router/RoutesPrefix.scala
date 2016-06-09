
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/skypage/Desktop/scala/scalajs-spa/server/src/main/resources/routes
// @DATE:Wed Jun 08 21:05:10 PDT 2016


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
