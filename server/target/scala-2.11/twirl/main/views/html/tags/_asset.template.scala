
package views.html.tags

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object _asset_Scope0 {
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

class _asset extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(path: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*1.17*/{play.api.Play.current.configuration.getString("application.cdn").getOrElse("") + routes.Assets.versioned(path)}))
      }
    }
  }

  def render(path:String): play.twirl.api.HtmlFormat.Appendable = apply(path)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (path) => apply(path)

  def ref: this.type = this

}


}

/**/
object _asset extends _asset_Scope0._asset
              /*
                  -- GENERATED --
                  DATE: Wed Jun 08 21:05:10 PDT 2016
                  SOURCE: /Users/skypage/Desktop/scala/scalajs-spa/server/src/main/twirl/views/tags/_asset.scala.html
                  HASH: 1e900805ff2c9e9a155c8429f69cf24574faf264
                  MATRIX: 534->1|643->16
                  LINES: 20->1|25->1
                  -- GENERATED --
              */
          