package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Trait
import models.charlist.Charlist.traitFormat
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
  def save(cTrait: Trait): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(name: String): Future[JsValue]
}

@Singleton
class MongoTraitDao @Inject()(mongo: Mongo) extends TraitDao {
  private val traits: MongoCollection[Document] = mongo.db getCollection "traits"
  private val toDoc: Trait => Document = x => Document(Json toJson x toString())
  private val docNameToJson: Document => JsObject = doc => Json obj ("name" -> doc.get("name").get.asString.getValue)

  override def save(cTrait: Trait): Future[Completed] = traits insertOne toDoc(cTrait) head()

  override def find(): Future[Seq[JsObject]] = traits find() map docNameToJson toFuture()

  override def find(name: String): Future[JsValue] =
    traits find Filters.equal("name", name) head() map (Json parse _.toJson())
}
