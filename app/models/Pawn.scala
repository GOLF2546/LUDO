package models

case class Pawn(
  PawnId: Int,
  initialX: Int,
  initialY: Int,
  color: Color
)

object PawnFunctions {
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
    pawn.initialX == 0 && pawn.initialY == 0
}