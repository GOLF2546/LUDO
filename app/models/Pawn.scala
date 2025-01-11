// Save as e:/Functional_Project/LUDO/app/models/Pawn.scala
package models

case class Pawn(initialX: Int, initialY: Int, color: Color) {
  private var positionX: Int = initialX
  private var positionY: Int = initialY

  def getPosition: (Int, Int) = (positionX, positionY)
  
  def move(steps: Int): (Int, Int) = {
    positionX += steps
    getPosition
  }

  def moveTo(x: Int, y: Int): Unit = {
    positionX = x
    positionY = y
  }

  override def toString: String = s"Pawn at ($positionX, $positionY) with color ${color.name}"
}