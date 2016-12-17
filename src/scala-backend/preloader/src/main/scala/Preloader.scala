import models.charlist._
import org.mongodb.scala.{Document, MongoClient}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by crimson on 11/15/16.
  */
object Preloader extends App {
  private def parse[A](p: Parser[A]): Seq[Document] = p.seq map { x => Document(Json.toJson(x)(p.tjs).toString) }

  private def load(client: MongoClient, collection: String, items: Seq[Document]) = {
    println(s"""Clearing db "$db" collection "$collection"...""")
    Await ready(client getDatabase db getCollection collection drop() toFuture(), 10.seconds) onSuccess {
      case Seq(_) => println("Cleared.")
    }
    println(s"Loading basic $collection...")
    Await ready(client getDatabase db getCollection collection insertMany items toFuture(), 30.seconds) onSuccess {
      case Seq(_) => println(s"""Basic $collection loaded to mongo db "$db" collection "$collection".""")
    }
  }

  println("Opening connection...")
  private val db = "gurps"
  private val client = MongoClient()
  load(client, "traits", parse[FlaggedTrait](new TraitsParser("/adv.xml")))
  load(client, "skills", parse[FlaggedSkill](new SkillsParser("/skl.xml")))
  load(client, "armor", parse[Armor](new ArmorParser("/eqp.xml")))
  load(client, "weapons", parse[Weapon](new WeaponsParser("/eqp.xml")))
  load(client, "items", parse[Item](new ItemsParser("/eqp.xml")))
  client close()
}