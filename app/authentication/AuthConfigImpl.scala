package authentication

import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current

import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc.{ RequestWithAttributes, RequestAttributeKey, StackableController }
import reflect.classTag

import controllers._
import models._
import dao._

trait AuthConfigImpl extends AuthConfig {

  type Id = String

  type User = Account

  type Authority = Permission

  val idTag = classTag[Id]

  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = {
    // Future.successful()
    Account.resolveUserById(id)
  }

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.GoogleNewsFeedCtrl.index))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Ok("Successfully logged out"))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Ok("Authentication failed"))

  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Ok("No permission"))

  def authorize(account: this.User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] =
    Future.successful(
      (account.permission, authority) match {
        case (Administrator, _) => true
        case (NormalUser, NormalUser) => true
        case _ => false

      })

  override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)
}

case class Account(email: String, password: String, permission: Permission)

object Account {

  def resolveUserById(id: String): Future[Option[Account]] = {

    val futureUser: Future[Option[User]] = UserDAO.findUserById(id)

    futureUser.map { e =>
      e match {
        case None => None
        case u: Some[User] => {
          val permission = Permission.valueOf(u.get.role)
          val userAccount = Account(u.get.email, u.get.password, permission)
          Option[Account](userAccount)
        }
      }
    }

  }

  def authenticate(email: String, password: String): Future[Option[Account]] = {

    val futureUser: Future[Option[User]] = UserDAO.findUserByEmail(email)

    futureUser.map { e =>

      val result: Option[User] = e.filter(u => u.password == password)

      result match {
        case None => None
        case u: Some[User] => {
          val permission = Permission.valueOf(u.get.role)
          val userAccount = Account(u.get.email, u.get.password, permission)
          Option[Account](userAccount)
        }
      }
    }
  }

}

