package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist.flaggedTraitFormat
import models.charlist.FlaggedTrait
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 11/16/16.
  */
@ImplementedBy(classOf[MongoTraitDao])
trait TraitDao {
  def save(fTrait: FlaggedTrait): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoTraitDao @Inject()(mongo: Mongo) extends TraitDao {
  private val traits: MongoCollection[Document] = mongo.db getCollection "traits"
  private val toDoc: FlaggedTrait => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "traitt").get.asDocument.get("name").asString.getValue)

  override def save(fTrait: FlaggedTrait): Future[Completed] = traits insertOne toDoc(fTrait) head()

  override def find(): Future[Seq[JsObject]] = traits find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    traits find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson())

  override def find(category: String, term: String): Future[Seq[JsObject]] =
    traits find Filters.eq("traitt.category", category) withFilter {
      _.get("traitt").get.asDocument.get("name").asString.getValue.toLowerCase.contains(term.toLowerCase)
    } map docIdToJson toFuture()
}