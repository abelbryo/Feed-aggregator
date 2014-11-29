import play.api._
import scala.concurrent.ExecutionContext.Implicits.global
import jobs._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    play.Logger.debug("Application has started")

    FeedFetchScheduler.startJob

    InitData.insert

  }

  override def onStop(app: Application) {
    play.Logger.debug("Application has stopped")
  }

}

// Inserting initial data for testing
object InitData {
  import dao._
  import models._

  def insert {
    val user = User("Administrator", "Abel", "Terefe", "abelbryo", "123456789", "abelbryo@gmail.com", "Betonimiehenkuja 6")

    val futureUser = UserDAO.findUserByUsername(user.username)

    futureUser.map { u: Option[User] =>
      u match {
        case None => UserDAO.insert(user)
        case Some(r) => // Do nothing
      }
    }

  }

}
