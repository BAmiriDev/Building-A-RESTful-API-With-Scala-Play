package controllers

import connectors.LibraryConnector
import controllers.models.{Book, APIError}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json, OFormat}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import services.LibraryService
import cats.data.EitherT
import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends AnyWordSpec with Matchers with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockConnector: LibraryConnector = mock[LibraryConnector]
  val testService = new LibraryService(mockConnector)

  // Define a Book instance for testing
  val book: Book = Book(
    title = "A Game of Thrones",
    authors = List("George R. R. Martin"),
    description = "The best book!!!",
    isbn = "someId"
  )

  "LibraryService" should {

    "return a book" in {
      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(*, *, *)
        .returning(EitherT.rightT(book)) // Using a Book instance
        .once()

      whenReady(testService.getGoogleBook(search = "game", term = "thrones").value) { result =>
        result shouldEqual Right(book) // Expecting a Book instance
      }
    }

    "return an error" in {
      val error = APIError.BadAPIResponse(500, "Internal Server Error")
      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(*, *, *)
        .returning(EitherT.leftT(error))
        .once()

      whenReady(testService.getGoogleBook(search = "invalid", term = "term").value) { result =>
        result shouldEqual Left(error)
      }
    }
  }
}
