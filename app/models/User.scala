package models

case class User(role: String,
  firstName: String,
  lastName: String,
  username: String,
  password: String,
  email: String,
  address: String,
  feeds: List[GoogleNewsFeed])

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  // Generates Writes and Reads for User using Json Macros
  implicit val userFormat = Json.format[User]
}
