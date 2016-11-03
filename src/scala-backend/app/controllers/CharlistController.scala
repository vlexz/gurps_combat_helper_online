package controllers

import com.google.inject.Inject
import daos.CharlistDao
import models.simplecharlist.Charlist
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Random

/**
  * Created by crimson on 9/23/16.
  */
class CharlistController @Inject()(charlistDao: CharlistDao) extends Controller {
  val invReq = { e: JsError =>
    Future(BadRequest(Json toJson (e.errors map { x => Json.obj(x._1.toString -> x._2.mkString("; ")) }))) }
  val throwMsg: PartialFunction[Throwable, Future[Result]] = {
    case e: IllegalStateException => Future(NotFound(Json.obj("message" -> e.getMessage)))
    case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
  }

  def options(id: String): Action[AnyContent] = Action.async { implicit request =>
    val methods = request.path.replaceAll(id, "") match {
      case "/api/char" => "GET, POST"
      case "/api/chars" => "GET"
      case "/api/char/" => "GET, PUT, PATCH, DELETE"
    }
    val requestHeaders = request.headers get ACCESS_CONTROL_REQUEST_HEADERS getOrElse ""
    Future {
      Ok.withHeaders(
        ALLOW -> methods,
        ACCESS_CONTROL_ALLOW_METHODS -> methods,
        ACCESS_CONTROL_ALLOW_HEADERS -> requestHeaders)
    }
  }

  def add(): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      request.body.validate[Charlist] match {
        case e: JsError => invReq(e)
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(
            _id = math.abs(Random.nextLong).toString,
            timestamp = System.currentTimeMillis.toString)
          charlistDao save charlist map { _: Completed => Ok(Json toJson charlist) } recoverWith throwMsg
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

  def create(): Action[AnyContent] = Action.async {
    Future(Ok(Json toJson Charlist()))
  }

  def replace(id: String): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      request.body.validate[Charlist] match {
        case e: JsError => invReq(e)
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(_id = id)
          charlistDao update charlist map { _: UpdateResult => Ok(Json toJson charlist) } recoverWith throwMsg
      }
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      charlistDao
        .find(id)
        .flatMap { j =>
          (j.as[JsObject] deepMerge request.body.as[JsObject]).validate[Charlist] match {
            case e: JsError => invReq(e)
            case s: JsSuccess[Charlist] =>
              val charlist = s.get.copy(_id = id)
              charlistDao update charlist map { _: UpdateResult => Ok(Json toJson charlist) } recoverWith throwMsg
          }
        }
        .recoverWith(throwMsg)
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    charlistDao delete id map { list: Seq[JsObject] => Ok(Json toJson list) } recoverWith throwMsg
  }
}
