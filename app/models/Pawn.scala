package models

case class Pawn(
  PawnId: Int,
  initialX: Int,
  initialY: Int,
  color: Color
)

object PawnFunctions {

  val setPosition: (Pawn, Color) => (Int, Int) = (pawn, color) => {
    color match {
      case Color.Blue   => (0, 0)  // Blue pawns start at (0, 0)
      case Color.Red    => (13, 0)  // Red pawns start at (13, 0)
      case Color.Green  => (26, 0)  // Green pawns start at (26, 0)
      case Color.Yellow => (39, 0)  // Yellow pawns start at (39, 0)
    }
  }


  val getPosition: Pawn => (Int, Int) = pawn => 
    (pawn.initialX, pawn.initialY)
  
  val move: (Pawn, Int, List[Pawn]) => (Int, Int) = (pawn, steps, otherPawns) => {
    val newX = pawn.initialX + steps
    checkPosition(newX, pawn.initialY, otherPawns)
    (newX, pawn.initialY)
  }

  val checkPosition: (Int, Int, List[Pawn]) => Unit = (newX, newY, otherPawns) => {
    otherPawns.find(pawn => getPosition(pawn) == (newX, newY)) foreach { pawn =>
      println(s"(0, 0) Pawn ${pawn.PawnId} ${pawn.color}")
    }
  }

  val isPawnAtStart: Pawn => Boolean = pawn => 
    //pawn.initialX == 0 && 
    pawn.initialY == 0
}