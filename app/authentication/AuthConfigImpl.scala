package authentication

import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current

import scala.concurrent._
import scala.concurrent.duration._
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

  def resolveUser(email: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = Account.resolveUserByEmail(email)

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.GoogleNewsFeedCtrl.index))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Application.login))

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

  override lazy val cookieSecureOption: Boolean =
    play.api.Play.current.configuration.getBoolean("auth.cookie.secure").getOrElse(true)
    // play.api.Play.isProd(play.api.Play.current)

}

case class Account(email: String, password: String, permission: Permission)

object Account {

  def resolveUserByEmail(email: String): Future[Option[Account]] = {

    val futureUser: Future[Option[User]] = UserDAO.findUserByEmail(email)

    futureUser.map { user:Option[User] =>
      user match {
        case None => None
        case Some(u) => {
          val permission = Permission.valueOf(u.role)
          val userAccount = Account(u.email, u.password, permission)
          play.Logger.debug(" === debug RESOLVING BY ID ===    " + userAccount.permission )
          Option[Account](userAccount)
        }
      }
    }

  }

  def authenticate(email: String, password: String): Option[Account] = {

    val futureUser: Future[Option[User]] = UserDAO.findUserByEmail(email)

    val resolvedUser = Await.result(futureUser, 1 minutes) // Block until user is resolved

    val result: Option[User] = resolvedUser.filter(u => u.password == password)

    result match {
      case None => None
      case Some(u) => {
        val permission = Permission.valueOf(u.role)
        val userAccount = Account(u.email, u.password, permission)
        Option[Account](userAccount)
      }
    }

  }

}

