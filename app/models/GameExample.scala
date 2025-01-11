// app/models/GameExample.scala
package models
import models.Color
import models.Dice
import models.Player
import models.Pawn

object GameExample extends App {
  val dice = Dice()
  val player = Player(1, "Alice", Color.Blue)
  val pawn = Pawn(0, 0, player.color)

  println(s"Player: $player")
  println(s"Initial pawn position: ${pawn.getPosition}")
  
  val steps = dice.roll()
  val newPosition = pawn.move(steps)
  println(s"After rolling $steps: $newPosition")
}