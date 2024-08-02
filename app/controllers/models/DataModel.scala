package controllers.models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     name: String,
                     description: String,
                     pageCount: Int,
                     isbn: String)

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val bookForm: Form[DataModel] = Form(
    mapping(
      "_id" -> nonEmptyText,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "pageCount" -> number,
      "isbn" -> nonEmptyText
    )(DataModel.apply)(DataModel.unapply)
  )
}






