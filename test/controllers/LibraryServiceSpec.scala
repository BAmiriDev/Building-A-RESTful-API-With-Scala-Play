package controllers

import baseSpec.{BaseSpec, BaseSpecWithApplication}
import controllers.models.{APIError, Book, DataModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import connectors.LibraryConnector
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import repositories.DataRepository
import services.LibraryService

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{


  val mockConnector: LibraryConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThronesJson: JsObject = Json.obj(
    "items" -> Json.arr(
      Json.obj(
        "id" -> "someId",
        "volumeInfo" -> Json.obj(
          "title" -> "A Game of Thrones",
          "description" -> "The best book!!!",
          "pageCount" -> 100,
          "industryIdentifiers" -> Json.arr(
            Json.obj(
              "type" -> "ISBN_13",
              "identifier" -> "1234567890"
            )
          )
        )
      )
    )
  )
  val gameOfThrones: Book = gameOfThronesJson.as[Book]

  "LibraryService" should {
    "return a DataModel when getGoogleBook is called" in {
      val url: String = "testUrl"
      val expectedDataModel = DataModel(
        _id = "someId",
        name = "A Game of Thrones",
        description = "The best book!!!",
        pageCount = 100,
        isbn = "1234567890"
      )

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThrones))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result.flatMap(Book.toDataModel(_).toRight(APIError.BadAPIResponse(500, "No items found in the book"))) mustBe Right(expectedDataModel)
      }
    }

    "return an error when getGoogleBook is called" in {
      val url: String = "testUrl"

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT[Future, Book](APIError.BadAPIResponse(500, "API Error")))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(APIError.BadAPIResponse(500, "API Error"))
      }
    }
  }
}