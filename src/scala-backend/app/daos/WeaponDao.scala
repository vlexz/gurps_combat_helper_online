package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist.weaponFormat
import models.charlist.Weapon
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 12/16/16.
  */
@ImplementedBy(classOf[MongoWeaponDao])
trait WeaponDao {
  def save(weapon: Weapon): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoWeaponDao @Inject()(mongo: Mongo) extends WeaponDao {
  private val weapons: MongoCollection[Document] = mongo.db getCollection "weapons"
  private val toDoc: Weapon => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "name").get.asString.getValue)

  override def save(weapon: Weapon): Future[Completed] = weapons insertOne toDoc(weapon) head()

  override def find(): Future[Seq[JsObject]] = weapons find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    weapons find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson)

  override def find(category: String, term: String): Future[Seq[JsObject]] = weapons find() withFilter {
    _.get("name").get.asString.getValue.toLowerCase.contains(term.toLowerCase)
  } map docIdToJson toFuture()
}