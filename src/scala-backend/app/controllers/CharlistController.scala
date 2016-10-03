package controllers

import java.util.NoSuchElementException

import com.google.inject.Inject
import models.charlist.Charlist
import org.mongodb.scala.MongoWriteException
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
      charlistService
        .save(
          request
            .body
            .validate[Charlist]
            .get
            .copy(_id = Random.nextLong.toString, timestamp = System.currentTimeMillis)
        )
        .map(re => Ok(Json.obj("success" -> re.toString)))
        .recoverWith {
          case e: MongoWriteException => Future(Forbidden)
          case _ => Future(Forbidden)
        }
    } catch {
      case e: NoSuchElementException => Future(BadRequest(Json.obj("message" -> e.getMessage)))
      case a: AssertionError => Future(BadRequest(Json.obj("message" -> s"Charlist ${a.getMessage}")))
      case t: Throwable => Future(InternalServerError)
    }
  )

  def list = Action.async(
    try {
      charlistService
        .find
        .map(cl => Ok(Json toJson cl))
        .recoverWith {
          case _ => Future(Forbidden)
        }
    } catch {
      case e: Exception => Future(BadRequest)
    }
  )

  def get(id: String) = Action.async(
    try {
      charlistService
        .find(id)
        .map(cl => Ok(cl))
        .recoverWith {
          case _ => Future(Forbidden)
        }
    } catch {
      case e: Exception => Future(BadRequest)
    }
  )

  def update = Action.async(BodyParsers.parse.json)(implicit request =>
    try {
      charlistService
        .update(
          request
            .body
            .validate[Charlist]
            .get
        )
        .map(re => Ok(Json.obj("success" -> re.toString)))
        .recoverWith {
          case e: MongoWriteException => Future(Forbidden)
          case _ => Future(Forbidden)
        }
    } catch {
      case e: Exception => Future(BadRequest)
    }
  )

  def delete(id: String) = Action.async(
    try {
      charlistService
        .delete(id)
        .map(re => Ok(Json.obj("success" -> re.toString)))
        .recoverWith {
          case e: MongoWriteException => Future(Forbidden)
          case _ => Future(Forbidden)
        }
    } catch {
      case e: Exception => Future(BadRequest)
    }
  )
}
