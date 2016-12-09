package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist.skillFormat
import models.charlist.Skill
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
@ImplementedBy(classOf[MongoSkillDao])
trait SkillDao {
  def save(skill: Skill): Future[Completed]

  def find(): Future[Seq[JsObject]]

  def find(id: String): Future[JsValue]

  def find(category: String, term: String): Future[Seq[JsObject]]
}

@Singleton
class MongoSkillDao @Inject()(mongo: Mongo) extends SkillDao {
  private val skills: MongoCollection[Document] = mongo.db getCollection "skills"
  private val toDoc: Skill => Document = x => Document(Json toJson x toString())
  private val docIdToJson: Document => JsObject = doc =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "name").get.asString.getValue) ++
      (Json obj "specialization" -> (doc get "spc").get.asString.getValue)

  override def save(skill: Skill): Future[Completed] = skills insertOne toDoc(skill) head()

  override def find(): Future[Seq[JsObject]] = skills find() map docIdToJson toFuture()

  override def find(id: String): Future[JsValue] =
    skills find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson)

  override def find(category: String, term: String): Future[Seq[JsObject]] =
    skills find() withFilter { s =>
      s.get("name").get.asString.getValue.toLowerCase.contains(term.toLowerCase) ||
        s.get("spc").get.asString.getValue.toLowerCase.contains(term.toLowerCase)
    } map docIdToJson toFuture()
}