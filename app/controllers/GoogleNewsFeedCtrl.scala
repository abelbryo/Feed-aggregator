package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController

import play.api.Play.current
import scala.concurrent.Future

import dao.FeedDAO
import models.JsonFormats._

object GoogleNewsFeedCtrl extends Controller with MongoController {

  def index = Action.async { implicit request =>
    val futureList = FeedDAO.getAllFeeds
    futureList.map { item => Ok(Json.toJson(item)) }
  } // -- END index

  def search = Action.async(parse.json) {
    implicit request =>
      val title = request.body.\("title").as[String]
      val futureResult = FeedDAO.getFeedByTitle(title)
      futureResult.map { item =>
        item match {
          case Nil => Ok(Json.toJson(Map("status" -> "Found Nothing 404")))
          case a: List[models.Feed] => Ok(Json.toJson(a))
        }
      } // end map
  }

  def searchContains = Action.async(parse.json){ implicit request =>
    val searchTerm = request.body.\("title").as[String]
    val futureResult = FeedDAO.getFeedByTitleContaining(searchTerm)

    futureResult.map { item =>
      item match {
        case Nil => Ok(Json.toJson(Map("status" -> "Found Nothing 404")))
        case a: List[models.Feed] => Ok(Json.toJson(a))
      }
    } // end map
  }

}

