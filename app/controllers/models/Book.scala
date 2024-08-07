package controllers.models

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

case class Book(items: List[Item])
object Book {
  implicit val format: OFormat[Book] = Json.format[Book]

  def toDataModel(book: Book): Option[DataModel] = {
    book.items.headOption.map { item =>
      DataModel(
        _id = item.id,
        name = item.volumeInfo.title,
        description = item.volumeInfo.description,
        pageCount = item.volumeInfo.pageCount,
        isbn = item.volumeInfo.industryIdentifiers.find(_.identifier.nonEmpty).map(_.identifier).getOrElse("")
      )
    }
  }
}
