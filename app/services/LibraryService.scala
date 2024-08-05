package services

import cats.data.EitherT
import connectors.LibraryConnector
import controllers.models.{APIError, DataModel}
import repositories.DataRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector, dataRepository: DataRepository) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)
                   (implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term")

    EitherT {
      connector.get[DataModel](url).value.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }

  def getByISBN(isbn: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
    EitherT {
      connector.get[DataModel](url).value.flatMap {
        case Right(dataModel) =>
          dataRepository.create(dataModel).map {
            case Right(savedModel) => Right(savedModel)
            case Left(error) => Left(error)
          }
        case Left(error) => Future.successful(Left(error))
      }.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }
}
