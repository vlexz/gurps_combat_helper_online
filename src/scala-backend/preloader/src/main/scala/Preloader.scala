import models.charlist.{FlaggedSkill, FlaggedTrait}
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
  client close()
}