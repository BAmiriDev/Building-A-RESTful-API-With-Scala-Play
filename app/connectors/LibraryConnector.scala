package connectors

import cats.data.EitherT
import controllers.models.APIError

import javax.inject.Inject
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.libs.json.OFormat

import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response
        .map {
          result =>
            Right(result.json.as[Response])
        }
        .recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect"))
        }
    }
  }

  // New method to fetch book data by ISBN
  def getByISBN[Response](isbn: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response
        .map { result =>
          Right(result.json.as[Response])
        }
        .recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect/ISBN"))
        }
    }
  }
}


