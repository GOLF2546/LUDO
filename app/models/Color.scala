package models

sealed abstract class Color

object Color {
  case object Blue extends Color
  case object Red extends Color
  case object Green extends Color
  case object Yellow extends Color
}
