package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist
import models.charlist.CharlistFields._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 9/23/16.
  */
@ImplementedBy(classOf[MongoCharlistDao])
trait CharlistDao {

  def save(charlist: Charlist): Future[Completed]

  def find: Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def update(charlist: Charlist): Future[UpdateResult]

  def delete(id: String): Future[DeleteResult]

}

@Singleton
class MongoCharlistDao @Inject()(mongo: Mongo) extends CharlistDao {

  private val charlists: MongoCollection[Document] = mongo.db.getCollection("charlist")

  override def save(charlist: Charlist): Future[Completed] = {
    charlists
      .insertOne(Document(Json.toJson(charlist).toString))
      .head
  }

  override def find: Future[Seq[JsObject]] = {
    charlists
      .find()
      .map[JsObject](documentToJsonHeader)
      .toFuture
  }

  override def find(id: String): Future[JsValue] = {
    charlists
      .find(Filters.equal(ID, id))
      .head
      .map[JsValue](doc => Json.parse(doc.toJson))
  }

  override def update(charlist: Charlist): Future[UpdateResult] = {
    charlists
      .updateOne(
        Filters.equal(ID, charlist._id),
        Document(Json.toJson(charlist).toString)
      )
      .head
  }

  override def delete(id: String): Future[DeleteResult] = {
    charlists
      .deleteOne(Filters.equal(ID, id))
      .head
  }

  private def documentToJsonHeader(doc: Document): JsObject = {
    Json.obj(
      ID -> doc.get(ID).get.asString.getValue,
      TIMESTAMP -> doc.get(TIMESTAMP).get.asInt64.getValue,
      PLAYER -> doc.get(PLAYER).get.asString.getValue,
      CPTOTAL -> doc.get(CPTOTAL).get.asInt32.getValue,
      NAME -> doc.get(NAME).get.asString.getValue
    )
  }

}
