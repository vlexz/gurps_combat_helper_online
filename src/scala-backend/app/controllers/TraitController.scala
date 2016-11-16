package controllers

import com.google.inject.Inject
import daos.TraitDao
import models.charlist.Trait
import models.charlist.Charlist.traitFormat
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.Future

/**
  * Created by crimson on 11/16/16.
  */
class TraitController @Inject()(traitDao: TraitDao) extends Controller {
  val throwMsg: PartialFunction[Throwable, Future[Result]] = {
    case e: IllegalStateException => Future(NotFound(Json.obj("Empty database return." -> e.toString)))
    case t: Throwable => Future(InternalServerError(t.toString))
  }

  def get(name: String): Action[AnyContent] = Action.async {
    traitDao find name map { t: JsValue => Ok(t) } recoverWith throwMsg
  }

  def list = Action.async {
    traitDao find() map { s: Seq[JsObject] => Ok(Json toJson s) } recoverWith throwMsg
  }

  def create: Action[AnyContent] = Action.async {
    Future(Created(Json toJson Trait()))
  }
}