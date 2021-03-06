package jobs

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.joda.time.DateTime

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.ExecutionContext.Implicits.global

import models.Feed

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController

import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.Future

import dao.FeedDAO

object FetchFeeds {

  val simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
  val URL_RSS = "https://news.google.com/?q=Ethiopia&output=rss"
  val URL_JSONP = "http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=100&q=" + URLEncoder.encode(URL_RSS, "utf-8")

  def asyncFetch = {
    val holder = WS.url(URL_JSONP)
    val futureResponse = holder.get()

    futureResponse.map { response =>
      val jsonResp = (response.json \ "responseData" \ "feed" \ "entries")

      jsonResp match {
        case JsArray(entries) => {
          // entries is a Seq[JsValue] so we can use 'map'
          entries map { item =>
            val googleNewsFeedEntry = mkNewsFeed(item)
            checkExistenceAndInsertIntoDB(googleNewsFeedEntry)
          }
        }
        case _ => throw new Exception("Unable to parse response JSON.")
      }
    }
  }

  private def mkNewsFeed(item: JsValue): Feed = {
    val title = (item \ "title").as[String]
    val result = title.split("-")
    val restyledTitle = result.last.mkString.trim + " - " + result.init.mkString.trim
    val url = (item \ "link").as[String]
    val link = cleanLink(url)
    val description = (item \ "contentSnippet").as[String]
    val pubDate = (item \ "publishedDate").as[String]
    val date = simpleDateFormat.parse(pubDate)
    val dateTime = new DateTime(date)

    Feed(restyledTitle, link, description, Option(dateTime))
  }

  private def cleanLink(link: String): String = {
    val pattern = "url=.*$".r
    val matches = pattern.findFirstIn(link)
    val result = matches.getOrElse("url=").replace("url=","")
    result
  }

  private def checkExistenceAndInsertIntoDB(feed: Feed){
    val isAlreadyInDB = FeedDAO.getFeedByTitle(feed.title)

    isAlreadyInDB.map {entry =>
      entry match {
        case Nil => {
          FeedDAO.persistFeed(feed)
          play.Logger.debug("Insert DONE ... ")
        }
        case a : List[models.Feed] => // Do nothing
      }
    }
  }

}
