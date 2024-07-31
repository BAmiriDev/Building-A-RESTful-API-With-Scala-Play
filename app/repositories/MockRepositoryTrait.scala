package repositories

import com.google.inject.ImplementedBy
import controllers.models.{APIError, DataModel}
import play.api.libs.json.JsValue
import scala.concurrent.Future


@ImplementedBy(classOf[DataRepository]) // Specifies the default implementation
trait MockRepositoryTrait {
  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]]
  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]]
  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, Long]]
  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]]
  def deleteAll(): Future[Unit]
  def findByName(name: String): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def updateField(id: String, fieldName: String, newValue: JsValue): Future[Either[APIError.BadAPIResponse, Long]]
}
