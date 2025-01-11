// Save as e:/Functional_Project/LUDO/app/models/Color.scala
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

  val values: List[Color] = List(Blue, Red, Green, Yellow)
  def fromName(name: String): Option[Color] = values.find(_.name.equalsIgnoreCase(name))
}