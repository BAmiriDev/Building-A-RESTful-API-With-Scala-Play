package services
import cats.data.EitherT
import connectors.LibraryConnector
import controllers.models.{Book, APIError}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)
                   (implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term")

    EitherT {
      connector.get[Book](url).map[Either[APIError, Book]] {
        book => Right(book)
      }.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }
}






