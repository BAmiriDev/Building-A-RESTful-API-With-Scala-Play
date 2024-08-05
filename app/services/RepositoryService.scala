
package services


import controllers.models.{APIError, DataModel}
import play.api.libs.json.JsValue
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(repository: DataRepository)(implicit ec: ExecutionContext) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    repository.index()

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    repository.create(book)

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    repository.read(id)

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, Long]] =
    repository.update(id, book)

  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]] =
    repository.delete(id)

  def deleteAll(): Future[Unit] =
    repository.deleteAll()

  def findByName(name: String): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    repository.findByName(name)

  def updateField(id: String, fieldName: String, newValue: JsValue): Future[Either[APIError.BadAPIResponse, Long]] =
    repository.updateField(id, fieldName, newValue)

}
