package controllers

import com.google.inject.Inject
import daos.CharlistDao
import models.charlist.Charlist
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.Future
import scala.util.Random

/**
  * Created by crimson on 9/23/16.
  */
class CharlistController @Inject()(charlistDao: CharlistDao) extends Controller {

  def add() = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      request
        .body
        .validate[Charlist] match {
        case e: JsError => Future(BadRequest(Json.obj("message" -> "Invalid request body.")))
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(
            _id = Random.nextLong.toString,
            timestamp = System.currentTimeMillis
          )
          charlistDao
            .save(charlist)
            .map(re => Ok(Json toJson charlist))
            .recoverWith { case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage))) }
      }
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def list = Action.async {
    charlistDao
      .find
      .map(cl => Ok(Json toJson cl))
      .recoverWith { case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage))) }
  }

  def get(id: String) = Action.async {
    try {
      charlistDao
        .find(id)
        .map(cl => Ok(cl))
        .recoverWith { case e: NoSuchElementException => Future(NotFound(Json.obj("message" -> e.getMessage))) }
    } catch {
      case e: NoSuchElementException => Future(NotFound(Json.obj("message" -> e.getMessage)))
      // TODO: make it recover properly
    }
  }

  def create() = Action.async {
    Future(Ok(Json toJson Charlist()))
  }

  def replace(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      request
        .body
        .validate[Charlist] match {
        case e: JsError => Future(BadRequest(Json.obj("message" -> "Invalid request body.")))
        case s: JsSuccess[Charlist] =>
          val charlist = s.get.copy(_id = id)
          charlistDao
            .update(charlist)
            .map { re => Ok(Json toJson charlist) }
            .recoverWith {
              case e: NoSuchElementException => Future(NotFound(Json.obj("message" -> e.getMessage)))
              case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
            }
      }
    } catch {
      case e: NoSuchElementException => Future(NotFound(Json.obj("message" -> e.getMessage)))
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
      // TODO: make it recover properly
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    try {
      charlistDao
        .find(id)
        .flatMap { j =>
          (j.as[JsObject] deepMerge request.body.as[JsObject]).validate[Charlist] match {
            // TODO: add JsResultException handling; .as to .asOpt
            case e: JsError => Future(BadRequest(Json.obj("message" -> "Invalid request body.")))
            case s: JsSuccess[Charlist] =>
              val charlist = s.get.copy(_id = id)
              charlistDao
                .update(charlist)
                .map(re => Ok(Json toJson charlist))
                .recoverWith { case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage))) }
          }
        }
        .recoverWith { case t: NoSuchElementException => Future(NotFound(Json.obj("message" -> t.getMessage))) }
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
    }
  }

  def delete(id: String) = Action.async {
    charlistDao
      .delete(id)
      .map { re => Ok(Json.obj("success" -> re.toString)) }
      .recoverWith { case t: NoSuchElementException => Future(NotFound(Json.obj("message" -> t.getMessage))) }
  }
}
