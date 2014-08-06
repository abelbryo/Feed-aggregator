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

import models.Feed
import models.JsonFormats._

object FeedDAO {

  lazy val collection = ReactiveMongoPlugin.db.collection[JSONCollection]("googlenewsfeed")

  def getAllFeeds = {
    val query = Json.obj() // query
    val filter = Json.obj() // projection
    val cursor: Cursor[Feed] = collection.
      find(query, filter).
      sort(Json.obj("pubDate" -> -1)).
      cursor[Feed]
    val futureList: Future[List[Feed]] = cursor.collect[List]()
    futureList
  }

  def persistFeed(feed: Feed) = {
    collection.insert(feed)
  }

  def getFeedByTitle(title: String) = {
    val cursor: Cursor[Feed] = collection.
      find(Json.obj("title" -> title)).
      sort(Json.obj("pubDate" -> -1)).
      cursor[Feed]
    val futureResult = cursor.collect[List]()
    futureResult
  }

  def getFeedByTitleContaining(searchTerm: String) = {
    val query = Json.obj("title" -> Json.obj("$regex" -> (".*" + searchTerm + ".*"), "$options" -> "i"))
    val filter = Json.obj()
    val result: Future[List[Feed]] = collection.
      find(query, filter).
      sort(Json.obj("pubDate" -> -1)).
      cursor[Feed].collect[List]()
    result
  }

  def getFeedById(id: String) = {
    val objectID = Json.obj("$oid" -> id)
    val futureFeed = collection.find(Json.obj("_id" -> objectID)).one[Feed]
    futureFeed
  }

}
