package controllers

import java.io.File
import com.google.inject.Inject
import com.sksamuel.scrimage.Image
import daos.CharlistDao
import models.simplecharlist.Charlist._
import models.simplecharlist._
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult
import play.api.Configuration
import play.api.libs.Files.TemporaryFile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future
import scala.util.Random

/**
  * Created by crimson on 9/23/16.
  */
class CharlistController @Inject()(charlistDao: CharlistDao, configuration: Configuration) extends Controller {
  val invalid = { e: JsError =>
    Future(BadRequest(Json toJson (e.errors map { x => Json.obj(x._1.toString -> x._2.mkString("; ")) })))
  }
  val throwMsg: PartialFunction[Throwable, Future[Result]] = {
    case e: IllegalStateException => Future(NotFound(Json.obj("Empty database return." -> e.toString)))
    case t: Throwable => Future(InternalServerError(t.toString))
  }
  val picFile = { id: String => new File(s"${configuration.underlying getString "files.pic"}$id.png") }

  def options(p: String, id: String = ""): Action[AnyContent] = Action { implicit request =>
    val methods = p match {
      case "base" => "GET, POST"
      case "list" => "GET"
      case "elem" => "GET, PUT, PATCH, DELETE"
      case "file" => "GET, PUT"
    }
    val requestHeaders = request.headers get ACCESS_CONTROL_REQUEST_HEADERS getOrElse ""
    Ok.withHeaders(
      ALLOW -> methods,
      ACCESS_CONTROL_ALLOW_METHODS -> methods,
      ACCESS_CONTROL_ALLOW_HEADERS -> requestHeaders)
  }

  def add(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    try {
      request.body.validate[Charlist] match {
        case e: JsError => invalid(e)
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(
            _id = math.abs(Random.nextLong).toString,
            timestamp = System.currentTimeMillis.toString)
          charlistDao save charlist map { _: Completed => Accepted(Json toJson charlist) } recoverWith throwMsg
      }
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def list: Action[AnyContent] = Action.async {
    charlistDao find() map { list: Seq[JsObject] => Ok(Json toJson list) } recoverWith throwMsg
  }

  def get(id: String): Action[AnyContent] = Action.async {
    charlistDao find id map { charlist: JsValue => Ok(charlist) } recoverWith throwMsg
  }

  def create(p: String): Action[AnyContent] = Action.async {
    lazy val payload = p match {
      case "char" => Json toJson Charlist()
      case "trait" => Json toJson Trait()
      case "skill" => Json toJson Skill()
      case "teq" => Json toJson Technique()
      case "weap" => Json toJson Weapon()
      case "armor" => Json toJson Armor()
      case "item" => Json toJson Item()
    }
    Future(Created(payload))
  }

  def update(id: String, replace: Boolean): Action[JsValue] = Action.async(parse.json) { implicit request =>
    try {
      def save(ch: JsValue): Future[Result] = ch.validate[Charlist] match {
        case e: JsError => invalid(e)
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(_id = id)
          charlistDao update charlist map { _: UpdateResult => Accepted(Json toJson charlist) } recoverWith throwMsg
      }
      lazy val b = request.body
      if (replace) save(b)
      else charlistDao find id flatMap { j => save(j.as[JsObject] deepMerge b.as[JsObject]) } recoverWith throwMsg
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    picFile(id).delete()
    charlistDao delete id map { list => Ok(Json toJson list) } recoverWith throwMsg
  }

  def storePic(id: String): Action[MultipartFormData[TemporaryFile]] = Action.async(parse.multipartFormData) {
    implicit request =>
      def tryMove = request.body file "pic" map { p =>
        val pf = picFile(id)
        if (pf.exists) pf.delete()
        Image fromFile p.ref.file cover(120, 150) output pf
      } match {
        case s: Some[File] => Accepted("Pic uploaded.")
        case None => BadRequest("Missing file.")
      }
      charlistDao exists id map { e => if (e) tryMove else NotFound("Charlist doesn't exist.") } recoverWith throwMsg
  }

  def getPic(id: String): Action[AnyContent] = Action {
    val pf = picFile(id)
    val dpf = new File(getClass.getResource(configuration.underlying getString "url.defaultpic").getPath)
    Ok sendFile(if (pf.exists) pf else dpf, inline = true)
  }
}
