package models

import org.joda.time.DateTime

case class Feed(
  title: String,
  link: String,
  description: String,
  pubDate: Option[DateTime])

  object JsonFormats {
    import play.api.libs.json.Json
    import play.api.data._
    import play.api.data.Forms._

    implicit val feedFormat = Json.format[Feed]
    implicit val userFormat = Json.format[User]
  }


