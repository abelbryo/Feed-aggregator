package dao

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.GoogleNewsFeed
import models.GoogleNewsFeed._

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api._
import play.modules.reactivemongo.MongoController

import reactivemongo.bson._
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentIdentity
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer

object FeedDAO {

  lazy val collection = ReactiveMongoPlugin.db.collection[BSONCollection]("googlenewsfeed")

  def getAllFeeds = {
    val query = BSONDocument() // query
    val filter = BSONDocument() // projection
    val cursor = collection.find(query, filter).cursor[GoogleNewsFeed]
    val futureList = cursor.collect[List]()
    futureList
  }

  def persistFeed(googleNewsFeedEntry: GoogleNewsFeed) = {
    collection.insert(googleNewsFeedEntry)
  }

  def getFeedByTitle(title: String) = {
    val cursor: Cursor[GoogleNewsFeed] = collection.
      find(BSONDocument("title" -> title)).
      sort(BSONDocument("pubDate" -> -1)).
      cursor[GoogleNewsFeed]
    val futureResult = cursor.collect[List]()
    futureResult
  }

  def getFeedByTitleContaining(searchTerm: String) = {
    val query = BSONDocument("title" -> BSONDocument("$regex" -> (".*" + searchTerm + ".*"), "$options" -> "i"))
    val filter = BSONDocument()
    val result = collection.find(query, filter).cursor[GoogleNewsFeed].collect[List]()
    result
  }

  def getFeedById(id: String) = {
    val objectID = BSONObjectID(id)
    val futureFeed = collection.find(BSONDocument("_id" -> objectID)).one[GoogleNewsFeed]
    futureFeed
  }

}
