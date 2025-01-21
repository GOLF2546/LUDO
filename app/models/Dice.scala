package models

import scala.util.Random

case class Dice(sides: Int = 6) {
  val roll: () => Int = () => Random.nextInt(sides) + 1
}
