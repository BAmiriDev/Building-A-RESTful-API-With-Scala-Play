import connectors.LibraryConnector
import controllers.models.Book
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.test.Helpers.stubControllerComponents

import scala.concurrent.{ExecutionContext, Future}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import services.LibraryService

class LibraryServiceSpec extends AnyWordSpec with Matchers with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  // Get the ExecutionContext from the injected components
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockConnector: LibraryConnector = mock[LibraryConnector]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(Future.successful(gameOfThrones.as[Book]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "")) { result =>
        result shouldEqual gameOfThrones.as[Book]
      }
    }
    "return an error" in {
      val url: String = "testUrl"

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(Future.failed(new RuntimeException("Some error occurred"))) // Simulate an error
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").failed) { result =>
        result shouldBe a[RuntimeException]
        result.getMessage shouldBe "Some error occurred"
      }
    }


  }
}
