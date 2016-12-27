package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.charlist.Charlist._
import models.charlist._
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
@ImplementedBy(classOf[MongoCharlistFeatureDao])
trait CharlistFeatureDao {
  def save(feature: Flagged): Future[Completed]

  def find(col: String, id: String): Future[JsValue]

  def find(col: String, cat: Seq[String], term: String = ""): Future[Seq[JsObject]]
}

object MongoCollections {
  val TRAITS = "traits"
  val SKILLS = "skills"
  val TECHNIQUES = "techniques"
  val WEAPONS = "weapons"
  val ARMORS = "armors"
  val ITEMS = "items"
}

@Singleton
class MongoCharlistFeatureDao @Inject()(mongo: Mongo) extends CharlistFeatureDao {

  import daos.MongoCollections._

  private val collMap: Map[String, MongoCollection[Document]] = Map(// TODO: make cached function
    TRAITS -> (mongo.db getCollection TRAITS),
    SKILLS -> (mongo.db getCollection SKILLS),
    TECHNIQUES -> (mongo.db getCollection TECHNIQUES),
    WEAPONS -> (mongo.db getCollection WEAPONS),
    ARMORS -> (mongo.db getCollection ARMORS),
    ITEMS -> (mongo.db getCollection ITEMS))
  private val strMap = Map(
    TRAITS -> "name",
    SKILLS -> "skillString",
    TECHNIQUES -> "tchString",
    WEAPONS -> "name",
    ARMORS -> "name",
    ITEMS -> "name")
  private val docIdToJson: (String, Document) => JsObject = (col, doc) =>
    (Json obj "id" -> (doc get "_id").get.asObjectId.getValue.toString) ++
      (Json obj "name" -> (doc get "data").get.asDocument.get(strMap(col)).asString.getValue)

  override def save(feature: Flagged): Future[Completed] = {
    def ins: (MongoCollection[Document], JsValue) => Future[Completed] =
      (c, j) => c insertOne Document(j.toString) head()

    feature match {
      case f: FlaggedTrait => ins(collMap(TRAITS), Json.toJson(f)(flaggedTraitFormat))
      case f: FlaggedSkill => ins(collMap(SKILLS), Json.toJson(f)(flaggedSkillFormat))
      case f: FlaggedTechnique => ins(collMap(TECHNIQUES), Json.toJson(f)(flaggedTechniqueFormat))
      case f: FlaggedWeapon => ins(collMap(WEAPONS), Json.toJson(f)(flaggedWeaponFormat))
      case f: FlaggedArmor => ins(collMap(ARMORS), Json.toJson(f)(flaggedArmorFormat))
      case f: FlaggedItem => ins(collMap(ITEMS), Json.toJson(f)(flaggedItemFormat))
    }
  }

  override def find(col: String, id: String): Future[JsValue] =
    collMap(col) find Filters.eq("_id", new ObjectId(id)) head() map (Json parse _.toJson())

  override def find(col: String, cat: Seq[String], term: String = ""): Future[Seq[JsObject]] = {
    val c = collMap(col)
    (if (cat != Nil) c find Filters.in("data.category", cat: _*) else c find()) withFilter {
      _.get("data").get.asDocument.get(strMap(col)).asString.getValue.toLowerCase.contains(term.toLowerCase)
    } map (docIdToJson(col, _)) toFuture()
  } // TODO: regex search
}
