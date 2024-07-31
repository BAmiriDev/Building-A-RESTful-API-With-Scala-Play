package services

import controllers.models.{APIError, DataModel}
import play.api.libs.json.JsValue
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(dataRepository: DataRepository)(implicit ec: ExecutionContext) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    dataRepository.index()

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    dataRepository.create(book)

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    dataRepository.read(id)

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, Long]] =
    dataRepository.update(id, book)

  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]] =
    dataRepository.delete(id)

  def deleteAll(): Future[Unit] =
    dataRepository.deleteAll()

  def findByName(name: String): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    dataRepository.findByName(name)

  def updateField(id: String, fieldName: String, newValue: JsValue): Future[Either[APIError.BadAPIResponse, Long]] =
    dataRepository.updateField(id, fieldName, newValue)

}
