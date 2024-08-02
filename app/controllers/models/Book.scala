package controllers.models

import play.api.libs.json.{Json, OFormat, JsValue}

case class Book(_id: String,
                name: String,
                description: String,
                pageCount: Int,
                isbn: String)

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]

  def fromGoogleBookJson(json: JsValue): Book = {
    val volumeInfo = (json \ "volumeInfo").get
    val industryIdentifiers = (volumeInfo \ "industryIdentifiers").as[Seq[JsValue]]
    val isbn = industryIdentifiers.find(id => (id \ "type").as[String] == "ISBN_13")
      .orElse(industryIdentifiers.find(id => (id \ "type").as[String] == "ISBN_10"))
      .map(id => (id \ "identifier").as[String])
      .getOrElse("")

    Book(
      _id = (json \ "id").as[String],
      name = (volumeInfo \ "title").as[String],
      description = (volumeInfo \ "description").asOpt[String].getOrElse(""),
      pageCount = (volumeInfo \ "pageCount").asOpt[Int].getOrElse(0),
      isbn = isbn
    )
  }
}
