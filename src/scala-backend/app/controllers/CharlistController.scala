package controllers

import java.util.NoSuchElementException

import com.google.inject.Inject
import models.charlist.Charlist
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers, Controller}
import services.CharlistService

import scala.concurrent.Future
import scala.util.Random

/**
  * Created by crimson on 9/23/16.
  */
class CharlistController @Inject()(charlistService: CharlistService) extends Controller {

  def add() = Action.async(BodyParsers.parse.json)(implicit request =>
    try {
      val id = Random.nextLong.toString
      val charlist =
        request
          .body
          .validate[Charlist]
          .get
          .copy(
            _id = Random.nextLong.toString,
            timestamp = System.currentTimeMillis
          )
      charlistService
        .save(charlist)
        .map(re => Ok(Json.toJson(charlist)))
        .recoverWith {
          case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
        }
    } catch {
      case e: NoSuchElementException => Future(BadRequest(Json.obj("message" -> e.getMessage)))
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )

  def list = Action.async(
    try {
      charlistService
        .find
        .map(cl => Ok(Json toJson cl))
        .recoverWith {
          case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
        }
    } catch {
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )

  def get(id: String) = Action.async(
    try {
      charlistService
        .find(id)
        .map(cl => Ok(cl))
        .recoverWith {
          case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
        }
    } catch {
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )

  def create() = Action.async(
    try {
      Future(Ok(Json toJson Charlist()))
    } catch {
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )

  def replace(id: String) = Action.async(BodyParsers.parse.json)(implicit request =>
    try {
      charlistService
        .update(
          request
            .body
            .validate[Charlist]
            .get
            .copy(_id = id)
        )
        .map(re => Ok(Json.obj("success" -> re.toString)))
        .recoverWith {
          case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
        }
    } catch {
      case e: NoSuchElementException => Future(BadRequest(Json.obj("message" -> e.getMessage)))
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )

  def delete(id: String) = Action.async(
    try {
      charlistService
        .delete(id)
        .map(re => Ok(Json.obj("success" -> re.toString)))
        .recoverWith {
          case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
        }
    } catch {
      case t: Throwable => Future(InternalServerError(Json.obj("message" -> t.getMessage)))
    }
  )
}
