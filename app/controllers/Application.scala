package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.reactivemongo.MongoController
import scala.concurrent.Future

import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc.{ RequestWithAttributes, RequestAttributeKey, StackableController }

import authentication._
import dao._
import models._

object Application extends Controller with MongoController with LoginLogout with AuthConfigImpl {

  val loginForm = Form(mapping(
    "email" -> email,
    "password" -> text)(Account.authenticate)(_.map(u => (u.email, "")))
    .verifying("Invalid email or password", result => result.isDefined))

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      user => gotoLoginSucceeded(user.get.email))
  }

  def index = Action {
    Ok(views.html.index("Hello Feeds"))
  }

}
