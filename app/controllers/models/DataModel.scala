package controllers.models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number, list}
import play.api.libs.json.{Json, OFormat}

case class IndustryIdentifier(`type`: String, identifier: String)
object IndustryIdentifier {
  implicit val format: OFormat[IndustryIdentifier] = Json.format[IndustryIdentifier]
}

case class VolumeInfo(
                       title: String,
                       description: String,
                       pageCount: Int,
                       industryIdentifiers: List[IndustryIdentifier]
                     )
object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}

case class Item(id: String, volumeInfo: VolumeInfo)
object Item {
  implicit val format: OFormat[Item] = Json.format[Item]
}

case class DataModel(items: List[Item])
object DataModel {
  implicit val format: OFormat[DataModel] = Json.format[DataModel]

  val bookForm: Form[DataModel] = Form(
    mapping(
      "items" -> list(mapping(
        "id" -> nonEmptyText,
        "volumeInfo" -> mapping(
          "title" -> nonEmptyText,
          "description" -> nonEmptyText,
          "pageCount" -> number,
          "industryIdentifiers" -> list(mapping(
            "type" -> nonEmptyText,
            "identifier" -> nonEmptyText
          )(IndustryIdentifier.apply)(IndustryIdentifier.unapply))
        )(VolumeInfo.apply)(VolumeInfo.unapply)
      )(Item.apply)(Item.unapply))
    )(DataModel.apply)(DataModel.unapply)
  )
}
