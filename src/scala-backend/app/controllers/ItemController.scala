package controllers

import com.google.inject.Inject
import daos.ItemDao
import models.charlist.Charlist.itemFormat
import models.charlist.Item
import org.mongodb.scala.Completed
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.Future

/**
  * Created by crimson on 12/17/16.
  */
class ItemController @Inject()(itemDao: ItemDao) extends Controller {
  private val invalidMsg = { e: JsError =>
    Future(BadRequest(Json toJson (e.errors map { case (a, b) => Json obj a.toString -> b.mkString("; ") })))
  }
  private val throwMsg: PartialFunction[Throwable, Future[Result]] = {
    case e: IllegalStateException => Future(NotFound(Json obj "Empty database return." -> e.toString))
    case t: Throwable => Future(InternalServerError(t.toString))
  }

  def options(p: String, name: String): Action[AnyContent] = Action { implicit request =>
    val methods = p match {
      case "base" => "GET, POST"
      case "list" => "GET"
      case "elem" => "GET"
    }
    val requestHeaders = request.headers get ACCESS_CONTROL_REQUEST_HEADERS getOrElse ""
    Ok withHeaders(
      ALLOW -> methods,
      ACCESS_CONTROL_ALLOW_METHODS -> methods,
      ACCESS_CONTROL_ALLOW_HEADERS -> requestHeaders)
  }

  def add(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Item] match {
      case e: JsError => invalidMsg(e)
      case s: JsSuccess[Item] =>
        itemDao save s.get map { _: Completed => Accepted(Json toJson s.get) } recoverWith throwMsg
    }
  }

  def create(): Action[AnyContent] = Action async Future(Created(Json toJson Item()))

  def list(): Action[AnyContent] = Action async
    (itemDao find() map { s: Seq[JsObject] => Ok(Json toJson s) } recoverWith throwMsg)

  def lookup(term: String): Action[AnyContent] = Action.async {
    itemDao find("", term) map {
      s: Seq[JsObject] => Ok(Json toJson s)
    } recoverWith throwMsg
  }

  def get(id: String): Action[AnyContent] =
    Action async (itemDao find id map { t: JsValue => Ok(t) } recoverWith throwMsg)
}
