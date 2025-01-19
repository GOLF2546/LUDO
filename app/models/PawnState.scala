package models


sealed abstract class PawnState
object PawnState {
  case object Start extends PawnState
  case object Finish extends PawnState 
  case object End extends PawnState 
  case object Normal extends PawnState 
}
