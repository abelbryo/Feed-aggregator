import play.api._
import jobs._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    play.Logger.debug("Application has started")
    FeedFetchScheduler.startJob
  }

  override def onStop(app: Application) {
    play.Logger.debug("Application has stopped")
  }

}
