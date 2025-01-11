// Save as e:/Functional_Project/LUDO/app/models/Dice.scala
package models

import scala.util.Random

case class Dice(sides: Int = 6) {
  def roll(): Int = Random.nextInt(sides) + 1
}