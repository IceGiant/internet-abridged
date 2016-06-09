
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object index_Scope0 {
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

class index extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(title: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {
import views.html.tags._

Seq[Any](format.raw/*1.17*/("""
"""),format.raw/*3.1*/("""
"""),format.raw/*4.1*/("""<!DOCTYPE html>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>"""),_display_(/*9.17*/title),format.raw/*9.22*/("""</title>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'> <!--Mobile syle-->
        <!-- <meta name="viewport" content="width=device-width, initial-scale=1"> --> <!--Non-mobile syle-->
        <link rel="stylesheet" media="screen" href="""),_display_(/*12.53*/_asset("stylesheets/main.min.css")),format.raw/*12.87*/(""" """),format.raw/*12.88*/(""">
        <link rel="shortcut icon" type="image/png" href="""),_display_(/*13.58*/_asset("images/favicon.png")),format.raw/*13.86*/(""" """),format.raw/*13.87*/(""">
    </head>
    <body>
        <div id="root">
        </div>
        """),_display_(/*18.10*/playscalajs/*18.21*/.html.scripts(projectName = "client", fileName => _asset(fileName).toString)),format.raw/*18.97*/("""
    """),format.raw/*19.5*/("""</body>
</html>
"""))
      }
    }
  }

  def render(title:String): play.twirl.api.HtmlFormat.Appendable = apply(title)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (title) => apply(title)

  def ref: this.type = this

}


}

/**/
object index extends index_Scope0.index
              /*
                  -- GENERATED --
                  DATE: Wed Jun 08 21:05:10 PDT 2016
                  SOURCE: /Users/skypage/Desktop/scala/scalajs-spa/server/src/main/twirl/views/index.scala.html
                  HASH: 20f05cc3aa33e1652bc8703234ce601f34fdc2a7
                  MATRIX: 527->1|661->16|688->43|715->44|833->136|858->141|1186->442|1241->476|1270->477|1356->536|1405->564|1434->565|1534->638|1554->649|1651->725|1683->730
                  LINES: 20->1|25->1|26->3|27->4|32->9|32->9|35->12|35->12|35->12|36->13|36->13|36->13|41->18|41->18|41->18|42->19
                  -- GENERATED --
              */
          