package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Trait
import models.charlist.Charlist.traitFormat
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoCollection}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.concurrent.Future
import services.Mongo

/**
  * Created by crimson on 11/16/16.
  */
@ImplementedBy(classOf[MongoTraitDao])
trait TraitDao {
  def save(cTrait: Trait): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]
}

@Singleton
class MongoTraitDao @Inject()(mongo: Mongo) extends TraitDao {
  private val traits: MongoCollection[Document] = mongo.db getCollection "traits"
  private val toDoc: Trait => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject =
    doc => Json obj (doc get "_id").get.asObjectId.getValue.toString -> (doc get "name").get.asString.getValue

  override def save(cTrait: Trait): Future[Completed] = traits insertOne toDoc(cTrait) head()

  override def find(): Future[Seq[JsObject]] = traits find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    traits find Filters.equal("_id", new ObjectId(id)) head() map (Json parse _.toJson())
}
