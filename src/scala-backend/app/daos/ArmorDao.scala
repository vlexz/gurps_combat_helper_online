package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Armor
import models.charlist.Charlist.armorElementFormat
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 12/14/16.
  */
@ImplementedBy(classOf[MongoArmorDao])
trait ArmorDao {
  def save(armor: Armor): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoArmorDao @Inject()(mongo: Mongo) extends ArmorDao {
  private val armors: MongoCollection[Document] = mongo.db getCollection "armor"
  private val toDoc: Armor => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "name").get.asString.getValue)

  override def save(armor: Armor): Future[Completed] = armors insertOne toDoc(armor) head()

  override def find(): Future[Seq[JsObject]] = armors find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    armors find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson)

  override def find(category: String, term: String): Future[Seq[JsObject]] = armors find() withFilter {
    _.get("name").get.asString.getValue.toLowerCase.contains(term.toLowerCase)
  } map docIdToJson toFuture()
}