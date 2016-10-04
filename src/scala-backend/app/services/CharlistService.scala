package services

import com.google.inject.Inject
import daos.CharlistDao
import models.charlist.Charlist
import org.mongodb.scala.Completed
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import play.api.libs.json.{JsObject, JsValue}

import scala.concurrent.Future

/**
  * Created by crimson on 9/23/16.
  */
class CharlistService @Inject()(charlistDao: CharlistDao) {

  def save(charlist: Charlist): Future[Completed] =
    charlistDao
      .save(charlist)

  def find: Future[Seq[JsObject]] =
    charlistDao
      .find

  def find(id: String): Future[JsValue] =
    charlistDao
      .find(id)

  def update(charlist: Charlist): Future[UpdateResult] =
    charlistDao
      .update(charlist)

  def delete(id: String): Future[DeleteResult] =
    charlistDao
      .delete(id)

}
