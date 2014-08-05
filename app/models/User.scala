package models

import org.joda.time.DateTime

case class User(role: String,
  firstName: String,
  lastName: String,
  username: String,
  password: String,
  email: String,
  address: String,
  feeds: List[Feed])


