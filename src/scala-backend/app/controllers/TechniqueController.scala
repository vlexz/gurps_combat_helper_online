package controllers

import com.google.inject.Inject
import daos.TechniqueDao
import models.charlist.{FlaggedTechnique, Technique}
import models.charlist.Charlist.{flaggedTechniqueFormat, techniqueFormat}
import org.mongodb.scala.Completed
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.Future

/**
  * Created by crimson on 12/17/16.
  */
class TechniqueController @Inject()(techniqueDao: TechniqueDao) extends Controller {
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
    request.body.validate[FlaggedTechnique] match {
      case e: JsError => invalidMsg(e)
      case s: JsSuccess[FlaggedTechnique] =>
        techniqueDao save s.get map { _: Completed => Accepted(Json toJson s.get) } recoverWith throwMsg
    }
  }

  def create(): Action[AnyContent] = Action async Future(Created(Json toJson Technique()))

  def list(): Action[AnyContent] = Action async
    (techniqueDao find() map { s: Seq[JsObject] => Ok(Json toJson s) } recoverWith throwMsg)

  def lookup(term: String): Action[AnyContent] = Action.async {
    techniqueDao find("", term) map {
      s: Seq[JsObject] => Ok(Json toJson s)
    } recoverWith throwMsg
  }

  def get(id: String): Action[AnyContent] =
    Action async (techniqueDao find id map { t: JsValue => Ok(t) } recoverWith throwMsg)
}