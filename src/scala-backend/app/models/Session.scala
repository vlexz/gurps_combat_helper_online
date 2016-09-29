package models

case class Session(
                    _id: String,
                    userId: String,
                    ip: String,
                    userAgent: String,
                    timestamp: Long,
                    lastActivity: Long
                  )

object Session {

  import play.api.libs.json.Json

  implicit val userFormat = Json.format[Session]
}