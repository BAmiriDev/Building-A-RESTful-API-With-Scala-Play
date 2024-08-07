package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.Helpers._
import play.api.test.{CSRFTokenHelper, FakeRequest}
import controllers.models.{APIError, DataModel}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader


import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends BaseSpecWithApplication {

  implicit val ec: ExecutionContext = executionContext
  val TestApplicationController = new ApplicationController(component, repository, service)(ec)

  // Define a sample DataModel object for testing
  private val dataModel: DataModel = DataModel(
    _id = "abcd",
    name = "test name",
    description = "test description",
    pageCount = 100,
    isbn = "1234567890"
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
      // Insert a DataModel for testing
      await(repository.create(dataModel))

      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
      contentAsJson(result).as[Seq[DataModel]] should contain(dataModel)
    }

    "handle empty list" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
      contentAsJson(result).as[Seq[DataModel]] shouldBe empty
    }
  }

  "ApplicationController .create" should {
    "create a book in the database" in {
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe CREATED
      contentAsJson(createdResult).as[DataModel] shouldBe dataModel
    }

    "return BadRequest for invalid data" in {
      val invalidRequest: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody(Json.obj())
      val result = TestApplicationController.create()(invalidRequest)

      status(result) shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .read" should {
    "find a book in the database by id" in {
      await(repository.create(dataModel))

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
    }

    "return NotFound for a non-existent book" in {
      val readResult: Future[Result] = TestApplicationController.read("nonexistent")(FakeRequest())
      status(readResult) shouldBe NOT_FOUND
    }
  }

  "ApplicationController .update" should {
    "update an existing DataModel" in {
      await(repository.create(dataModel))
      val updatedDataModel = dataModel.copy(name = "updated name")
      val updateRequest: FakeRequest[JsValue] = FakeRequest(PUT, s"/api/${dataModel._id}").withBody(Json.toJson(updatedDataModel))
      val updateResult: Future[Result] = TestApplicationController.update(dataModel._id)(updateRequest)

      status(updateResult) shouldBe ACCEPTED
    }

    "return BadRequest for an invalid update request" in {
      val invalidUpdateRequest: FakeRequest[JsValue] = FakeRequest(PUT, s"/api/${dataModel._id}").withBody(Json.obj())
      val updateResult: Future[Result] = TestApplicationController.update(dataModel._id)(invalidUpdateRequest)

      status(updateResult) shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .delete" should {
    "delete an existing DataModel" in {
      await(repository.create(dataModel))
      val deleteResult: Future[Result] = TestApplicationController.delete(dataModel._id)(FakeRequest())
      status(deleteResult) shouldBe ACCEPTED
    }

    "return BadRequest for an invalid delete request" in {
      val invalidDeleteResult: Future[Result] = TestApplicationController.delete("")(FakeRequest())
      status(invalidDeleteResult) shouldBe BAD_REQUEST
    }
  }

  // Additional test case for findByName method
  "ApplicationController .findByName" should {
    "find books in the database by name" in {
      await(repository.create(dataModel))
      val findByNameResult: Future[Result] = TestApplicationController.findByName(dataModel.name)(FakeRequest())
      status(findByNameResult) shouldBe OK
      contentAsJson(findByNameResult).as[Seq[DataModel]] should contain(dataModel)
    }

    "return NotFound for a non-existent book name" in {
      val findByNameResult: Future[Result] = TestApplicationController.findByName("nonexistent")(FakeRequest())
      status(findByNameResult) shouldBe NOT_FOUND
    }
  }

  // New tests for addBook and addBookForm methods
  "ApplicationController .addBook" should {
    "render the add book form" in {
      val request = FakeRequest(GET, "/addBook/form").withCSRFToken
      val result = TestApplicationController.addBook()(request)

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include("Add Book")
    }
  }

  "ApplicationController .addBookForm" should {
    //todo: fix this damn test
//    "process the add book form successfully" in {
//      val request = FakeRequest(POST, "/addBook/form")
//        .withFormUrlEncodedBody(
//          "_id" -> "3",
//          "name" -> "Test Book",
//          "description" -> "A test book description",
//          "pageCount" -> "100",
//          "isbn" -> "1234567890"
//        )
//        .withCSRFToken
//
//      val result = TestApplicationController.addBookForm()(request)
//
//      status(result) shouldBe SEE_OTHER
//      redirectLocation(result) shouldBe Some(routes.ApplicationController.index.url)
//    }



    "handle form submission errors" in {
      val request = FakeRequest(POST, "/addBook/form")
        .withFormUrlEncodedBody(
          "_id" -> "",
          "name" -> "Test Book",
          "description" -> "A test book description",
          "pageCount" -> "100",
          "isbn" -> "1234567890"
        )
        .withCSRFToken

      val result = TestApplicationController.addBookForm()(request)

      status(result) shouldBe BAD_REQUEST
    }
  }

}
