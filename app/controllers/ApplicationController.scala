package controllers
import controllers.models.{Book, DataModel}
import controllers.models.DataModel._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.LibraryService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       dataRepository: DataRepository,
                                       libraryService: LibraryService // Inject the LibraryService
                                     )(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(items: Seq[DataModel]) => Ok(Json.toJson(items))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }
  }


  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future.successful(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Some(dataModel) => Ok(Json.toJson(dataModel))
      case None => NotFound
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).map { updateResult =>
          if (updateResult.getModifiedCount > 0) Accepted else NotFound
        }
      case JsError(_) => Future.successful(BadRequest)
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    if (id.trim.isEmpty) {
      Future.successful(BadRequest(Json.obj("error" -> "Invalid ID")))
    } else {
      dataRepository.delete(id).map { deleteResult =>
        if (deleteResult.getDeletedCount > 0) Accepted else NotFound
      }
    }
  }


  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    libraryService.getGoogleBook(search = search, term = term).value.map {
      case Right(book) =>
        Ok(Json.toJson(book)) // Returns the book in JSON format with a 200 OK status
      case Left(error) =>
        InternalServerError(Json.toJson(s"Error fetching book: ${error.reason}")) // Returns an error message with a 500 Internal Server Error status
    }
  }


}
