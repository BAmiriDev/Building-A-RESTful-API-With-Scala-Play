package services
import play.api.libs.json.JsValue
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import connectors.LibraryConnector
import controllers.models.{APIError, Book}
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String): EitherT[Future, APIError, Book] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$term:$search")

    EitherT {
      connector.get[JsValue](url).value.map {
        case Right(json) =>
          val book = (json \ "items").as[Seq[JsValue]].headOption.map(Book.fromGoogleBookJson)
          book match {
            case Some(b) => Right(b)
            case None => Left(APIError.BadAPIResponse(404, "Book not found"))
          }
        case Left(apiError) => Left(apiError)
      }.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }




  def getByISBN(isbn: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
    EitherT {
      connector.get[Book](url).value.recover {
        case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
      }
    }
  }



}




