package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.simplecharlist.Charlist
import models.simplecharlist.CharlistFields._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.UpdateResult
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

  def delete(id: String): Future[Seq[JsObject]]

}

@Singleton
class MongoCharlistDao @Inject()(mongo: Mongo) extends CharlistDao {

  private val charlists: MongoCollection[Document] = mongo.db.getCollection("characters")
  private val documentToJsonHeader: Document => JsObject =
    doc => Json.obj(
      ID -> doc.get(ID).get.asString.getValue,
      TIMESTAMP -> doc.get(TIMESTAMP).get.asString.getValue,
      PLAYER -> doc.get(PLAYER).get.asString.getValue,
      NAME -> doc.get(NAME).get.asString.getValue)

  override def save(charlist: Charlist): Future[Completed] =
    charlists
      .insertOne(Document(Json.toJson(charlist).toString))
      .head

  override def find: Future[Seq[JsObject]] =
    charlists
      .find()
      .map[JsObject](documentToJsonHeader)
      .toFuture

  override def find(id: String): Future[JsValue] =
    charlists
      .find(Filters.equal(ID, id))
      .head
      .map[JsValue](doc => Json.parse(doc.toJson))

  override def update(charlist: Charlist): Future[UpdateResult] =
    charlists
      .replaceOne(
        Filters.equal(ID, charlist._id),
        Document(Json.toJson(charlist).toString)
      )
      .head

  override def delete(id: String): Future[Seq[JsObject]] = {
    charlists
      .deleteOne(Filters.equal(ID, id))
      .head
    charlists
      .find()
      .map[JsObject](documentToJsonHeader)
      .toFuture
  }
}
