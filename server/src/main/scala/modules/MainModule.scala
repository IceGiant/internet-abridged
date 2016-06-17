package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice._
import services.ApiService
import spa.shared.Api

/**
  * Created by skypage on 6/16/16.
  */
class MainModule extends AbstractModule with ScalaModule {
  def configure() = {
    bind[Api].to[ApiService]
  }
}
