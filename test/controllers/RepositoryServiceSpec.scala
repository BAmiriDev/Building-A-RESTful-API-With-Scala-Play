package controllers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import play.api.libs.json.JsValue
import repositories.{DataRepository, MockRepositoryTrait}
import controllers.models.{APIError, DataModel}
import org.scalamock.clazz.MockImpl.mock
import services.RepositoryService

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val mockRepository: MockRepositoryTrait = mock[MockRepositoryTrait]
  val repositoryService = new RepositoryService(mockRepository)// Pass the mocked trait

  val sampleDataModel: DataModel = DataModel(
    _id = "abcd",
    name = "test name",
    description = "test description",
    pageCount = 100,
    isbn = "1234567890"
  )


  "RepositoryService" should {
    "index - return all books" in {
      when(mockRepository.index()).thenReturn(Future.successful(Right(Seq(sampleDataModel))))

      val result = repositoryService.index().futureValue
      result shouldEqual Right(Seq(sampleDataModel))
    }

    "create - return created DataModel" in {
      when(mockRepository.create(any[DataModel])).thenReturn(Future.successful(Right(sampleDataModel)))

      val result = repositoryService.create(sampleDataModel).futureValue
      result shouldEqual Right(sampleDataModel)
    }

    "read - return DataModel by id" in {
      when(mockRepository.read("abcd")).thenReturn(Future.successful(Right(sampleDataModel)))

      val result = repositoryService.read("abcd").futureValue
      result shouldEqual Right(sampleDataModel)
    }

    "update - update DataModel and return success" in {
      when(mockRepository.update(any[String], any[DataModel])).thenReturn(Future.successful(Right(1L)))

      val result = repositoryService.update("abcd", sampleDataModel).futureValue
      result shouldEqual Right(1L)
    }

    "delete - delete DataModel and return success" in {
      when(mockRepository.delete("abcd")).thenReturn(Future.successful(Right(1L)))

      val result = repositoryService.delete("abcd").futureValue
      result shouldEqual Right(1L)
    }

    "deleteAll - delete all DataModels" in {
      when(mockRepository.deleteAll()).thenReturn(Future.successful(()))

      val result = repositoryService.deleteAll().futureValue
      result shouldEqual (())
    }

    "findByName - return books by name" in {
      when(mockRepository.findByName("test name")).thenReturn(Future.successful(Right(Seq(sampleDataModel))))

      val result = repositoryService.findByName("test name").futureValue
      result shouldEqual Right(Seq(sampleDataModel))
    }

    "updateField - update specific field and return success" in {
      val newValue: JsValue = mock[JsValue]
      when(mockRepository.updateField(any[String], any[String], any[JsValue])).thenReturn(Future.successful(Right(1L)))

      val result = repositoryService.updateField("abcd", "name", newValue).futureValue
      result shouldEqual Right(1L)
    }
  }
}
