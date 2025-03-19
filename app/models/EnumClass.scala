package models
import play.api.libs.json._

// Generic JSON format for enums
abstract class EnumJsonFormat[T] {
  def values: Map[String, T]

  implicit val format: Format[T] = new Format[T] {
    def writes(enumValue: T): JsValue = JsString(enumValue.toString)

    def reads(json: JsValue): JsResult[T] = json match {
      case JsString(value) =>
        values.get(value).map(JsSuccess(_)).getOrElse(JsError(s"Unknown value: $value"))
      case _ => JsError("Expected JSON string")
    }
  }
}

// Define Color as a sealed abstract class
sealed abstract class Color(val name: String) {
  override def toString: String = name
}

object Color extends EnumJsonFormat[Color] {
  case object Blue extends Color("Blue")
  case object Red extends Color("Red")
  case object Green extends Color("Green")
  case object Yellow extends Color("Yellow")

  override val values: Map[String, Color] = Seq(Blue, Red, Green, Yellow).map(c => c.name -> c).toMap
}

// Define PawnState as a sealed abstract class
sealed abstract class PawnState(val name: String) {
  override def toString: String = name
}

object PawnState extends EnumJsonFormat[PawnState] {
  case object Start extends PawnState("Start")
  case object Finish extends PawnState("Finish")
  case object End extends PawnState("End")
  case object Normal extends PawnState("Normal")

  override val values: Map[String, PawnState] = Seq(Start, Finish, End, Normal).map(s => s.name -> s).toMap
}

// Define implicit JSON formats inside an object
object EnumFormats {
  implicit val colorFormat: Format[Color] = Color.format
  implicit val pawnStateFormat: Format[PawnState] = PawnState.format
}
