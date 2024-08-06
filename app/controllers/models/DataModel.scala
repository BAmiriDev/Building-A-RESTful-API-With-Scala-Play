
package controllers.models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number, list}
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     name: String,
                     description: String,
                     pageCount: Int,
                     isbn: String)

object DataModel {
  implicit val format: OFormat[DataModel] = Json.format[DataModel]

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