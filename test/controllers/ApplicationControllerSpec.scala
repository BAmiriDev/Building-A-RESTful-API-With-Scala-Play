package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import repositories.DataRepository
import controllers.models.DataModel
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.Status
import play.api.mvc.{Result, Results}

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  // Inject the necessary components from BaseSpecWithApplication
  val repository: DataRepository = app.injector.instanceOf[DataRepository]

  // Create the TestApplicationController with the injected components
  val TestApplicationController = new ApplicationController(
    component,
    repository
  )

  // Define a sample DataModel object for testing
  private val dataModel: DataModel = DataModel(
    _id = "abcd",
    name = "test name",
    description = "test description",
    pageCount = 100
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(repository.deleteAll())
  }

  override def afterEach(): Unit = {
    await(repository.deleteAll())
    super.afterEach()
  }

  "ApplicationController .index" should {
    "return a list of DataModels with OK status" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
      // Additional assertions can be added here to verify the response
    }
  }

  "ApplicationController .create" should {
    "create a book in the database" in {
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe CREATED
      // Additional assertions can be added here to verify the created data
    }
  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {

      // Use create method to add a DataModel
      val createRequest: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe CREATED  // Checking the status after creation

      // Use read method to retrieve the DataModel by id
      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
    }
  }



  "ApplicationController .update()" should {
    "update an existing DataModel" in {
      // Implement test logic for the update method
    }
  }

  "ApplicationController .delete()" should {
    "delete a DataModel" in {
      // Implement test logic for the delete method
    }
  }
}
