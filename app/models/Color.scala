package models

sealed trait Color {
  def name: String
}

object Color {
  case object Blue extends Color {
    val name: String = "Blue"
  }
  case object Red extends Color {
    val name: String = "Red"
  }
  case object Green extends Color {
    val name: String = "Green"
  }
  case object Yellow extends Color {
    val name: String = "Yellow"
  }
}