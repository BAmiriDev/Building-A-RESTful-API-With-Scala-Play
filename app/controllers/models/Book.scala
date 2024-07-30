package controllers.models

import play.api.libs.json.{Json, OFormat}

case class Book(
                        title: String,
                        authors: List[String],
                        description: String,
                        isbn: String
                      )

object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}
