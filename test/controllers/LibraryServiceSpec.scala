package controllers

import controllers.models.{APIError, Book}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import connectors.LibraryConnector
import services.LibraryService

class LibraryServiceSpec extends PlaySpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  val mockConnector: LibraryConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook" should {

    "return a book when getGoogleBook is called" in {
      val url: String = "testUrl"
      val expectedBook = Book(
        _id = "someId",
        name = "A Game of Thrones",
        description = "The best book!!!",
        pageCount = 100,
        isbn = "1234567890"

      )

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThrones.as[Book]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result mustBe Right(expectedBook)
      }
    }

    "return an error when getGoogleBook is called" in {
      val url: String = "testUrl"

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT[Future, Book](APIError.BadAPIResponse(500, "API Error")))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result mustBe Left(APIError.BadAPIResponse(500, "API Error"))
      }
    }
  }
}
