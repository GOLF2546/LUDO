package models

import play.api.libs.json._

sealed abstract class PawnState
object PawnState {
  case object Start extends PawnState
  case object Finish extends PawnState
  case object End extends PawnState
  case object Normal extends PawnState

  implicit val pawnStateFormat: Format[PawnState] = new Format[PawnState] {
    def writes(state: PawnState): JsValue = JsString(state.toString)
    def reads(json: JsValue): JsResult[PawnState] = json match {
      case JsString("Start") => JsSuccess(Start)
      case JsString("Finish") => JsSuccess(Finish)
      case JsString("End") => JsSuccess(End)
      case JsString("Normal") => JsSuccess(Normal)
      case _ => JsError("Unknown state")
    }
  }
}
