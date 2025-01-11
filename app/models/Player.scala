// Save as e:/Functional_Project/LUDO/app/models/Player.scala
package models

case class Player(id: Int, name: String, color: Color) {
  override def toString: String = s"Player($id, $name, ${color.name})"
}