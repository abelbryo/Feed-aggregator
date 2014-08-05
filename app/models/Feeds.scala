package models

import play.api.libs.json.Json

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps

import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer

import org.joda.time.DateTime

case class GoogleNewsFeed(id: Option[BSONObjectID],
  title: String,
  link: String,
  description: String,
  pubDate: Option[DateTime])

object GoogleNewsFeed {
  implicit val newsFeedFormat = Json.format[GoogleNewsFeed]

  // serializing into BSON
  implicit object GoogleNewsFeedBSONWriter extends BSONDocumentWriter[GoogleNewsFeed] {
    def write(newsFeed: GoogleNewsFeed): BSONDocument =
      BSONDocument(
        "_id" -> newsFeed.id.getOrElse(BSONObjectID.generate),
        "title" -> newsFeed.title,
        "link" -> newsFeed.link,
        "description" -> newsFeed.description,
        "pubDate" -> BSONDateTime(newsFeed.pubDate.get.getMillis))
  }

  // de-serializing a GoogleNewsFeed from a BSON
  implicit object GoogleNewsFeedBSONReader extends BSONDocumentReader[GoogleNewsFeed] {
    def read(doc: BSONDocument): GoogleNewsFeed =
      GoogleNewsFeed(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("title").get,
        doc.getAs[String]("link").get,
        doc.getAs[String]("description").get,
        doc.getAs[BSONDateTime]("pubDate").map(dt => new DateTime(dt.value)))
  }
}
