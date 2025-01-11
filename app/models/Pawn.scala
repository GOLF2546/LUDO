package models

case class Pawn(initialX: Int, initialY: Int, color: Color) {
  private var positionX: Int = initialX
  private var positionY: Int = initialY

  val getPosition: (Int, Int) = (positionX, positionY)
  
  val move: Int => (Int, Int) = (steps: Int) => {
    positionX += steps
    (positionX, positionY)
  }

  val moveTo: (Int, Int) => Unit = (x: Int, y: Int) => {
    positionX = x
    positionY = y
  }

  override def toString: String = s"Pawn at ($positionX, $positionY) with color ${color.name}"
}