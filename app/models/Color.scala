package models

// import play.api.libs.json._

sealed abstract class Color
object Color {
  case object Blue extends Color
  case object Red extends Color
  case object Green extends Color
  case object Yellow extends Color

  // implicit val colorFormat: Format[Color] = new Format[Color] {
  //   def writes(color: Color): JsValue = JsString(color.toString)
  //   def reads(json: JsValue): JsResult[Color] = json match {
  //     case JsString("Blue") => JsSuccess(Blue)
  //     case JsString("Red") => JsSuccess(Red)
  //     case JsString("Green") => JsSuccess(Green)
  //     case JsString("Yellow") => JsSuccess(Yellow)
  //     case _ => JsError("Unknown color")
  //   }
  // }
}
