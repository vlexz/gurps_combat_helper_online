package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist.flaggedTechniqueFormat
import models.charlist.FlaggedTechnique
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import services.Mongo

import scala.concurrent.Future

/**
  * Created by crimson on 12/9/16.
  */
@ImplementedBy(classOf[MongoTechniqueDao])
trait TechniqueDao {
  def save(fTechnique: FlaggedTechnique): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoTechniqueDao @Inject()(mongo: Mongo) extends TechniqueDao {
  private val techniques: MongoCollection[Document] = mongo.db getCollection "techniques"
  private val toDoc: FlaggedTechnique => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "technique").get.asDocument.get("tchString").asString.getValue)

  override def save(fTechnique: FlaggedTechnique): Future[Completed] = techniques insertOne toDoc(fTechnique) head()

  override def find(): Future[Seq[JsObject]] = techniques find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    techniques find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson)

  override def find(category: String, term: String): Future[Seq[JsObject]] = techniques find() withFilter { s =>
    s.get("technique").get.asDocument.get("tchString").asString.getValue.toLowerCase.contains(term.toLowerCase)
  } map docIdToJson toFuture()
}