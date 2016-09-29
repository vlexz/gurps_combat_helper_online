package services

import com.google.inject.Inject
import daos.CharlistDao
import models.Charlist.CharlistData
import org.mongodb.scala.Completed
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.concurrent.Future

/**
  * Created by crimson on 9/23/16.
  */
class CharlistService @Inject()(charlistDao: CharlistDao) {

  def save(charlist: CharlistData): Future[Completed] =
    charlistDao
      .save(Json.toJson(charlist))

  def find: Future[Seq[JsObject]] =
    charlistDao
      .find

  def find(id: String): Future[JsValue] =
    charlistDao
      .find(id)

  def update(charlist: CharlistData): Future[UpdateResult] =
    charlistDao
      .update(Json.toJson(charlist))

  def delete(id: String): Future[DeleteResult] =
    charlistDao
      .delete(id)

}
