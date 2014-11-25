package dao

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.Play.current

import scala.concurrent.Future

import reactivemongo.api._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import models.User
import models.JsonFormats._

object UserDAO {
  def collection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  def insert(user: User) = {
    val futureResult = collection.insert(user)
    futureResult
  }

  def findByName(lastName: String) = {
    val cursor: Cursor[User] = collection.
      find(Json.obj("lastName" -> lastName)).
      sort(Json.obj("created" -> -1)).
      cursor[User]

    val futureUserList: Future[List[User]] = cursor.collect[List]()
    futureUserList
  }

  def findUserByUsername(username: String) = {
    val futureUser: Future[Option[User]] = collection.find(Json.obj("username" -> username)).one[User]
    futureUser
  }

  def findUserByEmail(email: String) = {
    val futureUser: Future[Option[User]] = collection.find(Json.obj("email" -> email)).one[User]
    futureUser
  }

  def findUserById(id: String) = {
    val userId = Json.obj("$oid" -> id)
    val futureUser: Future[Option[User]] = collection.find(userId).one[User]
    futureUser
  }


}
