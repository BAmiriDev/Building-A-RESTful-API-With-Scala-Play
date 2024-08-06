package services

import cats.data.EitherT
import connectors.LibraryConnector
import controllers.models.{APIError, Book, DataModel}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)
                   (implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term")

    EitherT {
      connector.get[Book](url).value.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }

  def getByISBN(isbn: String, dataRepository: DataRepository)
               (implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
    EitherT {
      connector.get[Book](url).value.flatMap {
        case Right(book) =>
          Book.toDataModel(book) match {
            case Some(dataModel) => dataRepository.create(dataModel)
            case None => Future.successful(Left(APIError.BadAPIResponse(500, "No items found in the book")))
          }
        case Left(error) => Future.successful(Left(error))
      }.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }
}
