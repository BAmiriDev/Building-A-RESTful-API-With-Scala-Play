package repositories

import controllers.models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.result
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection
      .insertOne(book)
      .toFuture()
      .map { _ =>
        Right(book)
      }.map(_ => Right(book)) // Assuming successful insertion means the book was created


  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )


  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(dataModel) => Right(dataModel)
      case None => Left(APIError.BadAPIResponse(404, "DataModel not found"))
    }


  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true)
    ).toFuture().map { updateResult =>
      if (updateResult.getModifiedCount > 0) Right(updateResult.getModifiedCount)
      else Left(APIError.BadAPIResponse(404, "DataModel not found or not modified"))
    }


  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture().map { deleteResult =>
      if (deleteResult.getDeletedCount > 0) Right(deleteResult.getDeletedCount)
      else Left(APIError.BadAPIResponse(404, "DataModel not found"))
    }


  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}
