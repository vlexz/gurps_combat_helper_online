package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist.itemFormat
import models.charlist.Item
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 12/17/16.
  */
@ImplementedBy(classOf[MongoItemDao])
trait ItemDao {
  def save(item: Item): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoItemDao @Inject()(mongo: Mongo) extends ItemDao {
  private val items: MongoCollection[Document] = mongo.db getCollection "items"
  private val toDoc: Item => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "name").get.asString.getValue)

  override def save(item: Item): Future[Completed] = items insertOne toDoc(item) head()

  override def find(): Future[Seq[JsObject]] = items find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    items find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson)

  override def find(category: String, term: String): Future[Seq[JsObject]] = items find() withFilter {
    _.get("name").get.asString.getValue.toLowerCase.contains(term.toLowerCase)
  } map docIdToJson toFuture()
}