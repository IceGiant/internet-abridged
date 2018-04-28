import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "scalajs-spa"

  /** The version of your application */
  val version = "1.1.2"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.11"
    val scalaz = "7.2.4"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.0"
    val scalajsJquery = "0.9.0"
    val scalaCSS = "0.4.1"
    val log4js = "1.4.10"
    val autowire = "0.2.5"
    val booPickle = "1.2.4"
    val diode = "1.0.0"
    val uTest = "0.4.3"

    val react = "15.0.1"
    val jQuery = "2.1.3"
    val bootstrap = "3.3.6"
    val bootswatch = "3.3.5+4"
    val fontAwesome = "4.6.3"
    val chartjs = "1.0.1"

    val playScripts = "0.5.0"

    val playSlick = "2.0.0"
    val slick = "3.1.1"
    val akka = "2.4.6"
    val playMailer = "5.0.0"
    val scalaGuice = "4.0.0"
    val h2db = "1.4.192"

    val dispatch = "0.11.2"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "me.chrons" %%% "boopickle" % versions.booPickle,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "play-scalajs-scripts" % versions.playScripts,
    "org.webjars" % "font-awesome" % versions.fontAwesome % Provided,
    "org.webjars" % "bootswatch-cyborg" % versions.bootswatch % Provided,
    //"org.webjars" % "bootswatch-darkly" % versions.bootswatch % Provided,
    //"org.webjars" % "bootstrap" % versions.bootstrap % Provided,
    "net.databinder.dispatch" %% "dispatch-core" % versions.dispatch,
    "com.typesafe.akka" %% "akka-cluster" % versions.akka,
    "com.typesafe.akka" %% "akka-cluster-tools" % versions.akka,
    "com.typesafe.akka" %% "akka-slf4j" % versions.akka,
    "net.codingwell" %% "scala-guice" % versions.scalaGuice,
    "com.typesafe.play" %% "play-mailer" % versions.playMailer,

    "com.typesafe.slick" %% "slick" % versions.slick,
    "com.typesafe.play" %% "play-slick" % versions.playSlick,
    "com.typesafe.play" %% "play-slick-evolutions" % versions.playSlick,
    "com.h2database" % "h2" % versions.h2db,

    "org.scalaz" %% "scalaz-core" % versions.scalaz

    //,"org.webjars.npm" % "react" % versions.react
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "me.chrons" %%% "diode" % versions.diode,
    "me.chrons" %%% "diode-react" % versions.diode,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "be.doeraene" %%% "scalajs-jquery" % versions.scalajsJquery
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars" % "bootswatch-cyborg" % versions.bootswatch / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    //"org.webjars" % "bootswatch-darkly" % versions.bootswatch / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    //"org.webjars" % "bootstrap" % versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    "org.webjars" % "chartjs" % versions.chartjs / "Chart.js" minified "Chart.min.js",
    "org.webjars" % "log4javascript" % versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
  ))
}
