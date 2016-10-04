package models.charlist

/**
  * Created by crimson on 10/4/16.
  */
case class CharlistHeader(
                           _id: String = "",
                           timestamp: Long = 0,
                           player: String = "",
                           cp: Int = 0,
                           name: String = ""
                         )

object CharlistHeader {

  import play.api.libs.json.Json

  implicit val charlistHeaderFormat = Json.format[CharlistHeader]
}