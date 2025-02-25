package models

import play.api.libs.json._

case class Pawn(
    PawnId: Int,
    initialX: Int,
    initialY: Int,
    color: Color,
    state: PawnState
)

object Pawn {
  implicit val pawnFormat: Format[Pawn] = Json.format[Pawn]
}

object PawnFunctions {

  val setPosition: (Color) => (Int) = (color) => {
    color match {
      case Color.Blue   => (0) // Blue pawns start at (0, 0)
      case Color.Red    => (13) // Red pawns start at (13, 0)
      case Color.Green  => (26) // Green pawns start at (26, 0)
      case Color.Yellow => (39) // Yellow pawns start at (39, 0)
    }
  }

  val getPosition: Pawn => (Int, PawnState) = pawn =>
    (pawn.initialX, pawn.state)

  val move: (Pawn, Int, List[Pawn]) => (Int, Int, PawnState) =
    (pawn, steps, otherPawns) => {
      val newY = pawn.initialY + steps
      println(
        s"new Y is ${newY} initialy is ${pawn.initialY} and step is ${steps}"
      )
      // Compute newX and state based on the current state and newY
      val (newX, newState) = (pawn.state, newY) match {
        case (PawnState.Start, y) if y == 6 =>
          println("Pawn is in Start state, moving to normal statr")
          (setPosition(pawn.color), PawnState.Normal)
        case (PawnState.Normal, y) if y > 10 =>
          val nextX = newY - 10
          println("Pawn is in Normal state, moving to the finish line.")
          (nextX, PawnState.Finish)

        case (PawnState.Finish, y) if y >= 5 =>
          println("Pawn has reached the End state.")
          (0, PawnState.End)

        case _ =>
          val nextX = pawn.initialX + steps
          println("No specific match, moving pawn normally.")
          (nextX, pawn.state)
      }
      // Perform position check
      checkPosition(newX, newState, otherPawns)

      // Return the new position and state
      (newX, newY, newState)
    }
  val checkPosition: (Int, PawnState, List[Pawn]) => List[Pawn] =
    (newX, state, otherPawns) => {
      otherPawns.map { pawn =>
        if (
          pawn.state == PawnState.Normal && getPosition(pawn) == (newX, state)
        ) {
          println(
            s"(0, 0) Pawn ${pawn.PawnId} ${pawn.color} is being reset to the Start state."
          )
          pawn.copy(state =
            PawnState.Start
          ) // Create a new Pawn with the updated state
        } else {
          pawn // Keep the original Pawn unchanged
        }
      }
    }


  val isPawnAtStart: Pawn => Boolean = pawn =>
    // pawn.initialX == 0 &&
    pawn.state == PawnState.Start

  val isPawnCanMove: Pawn => Boolean = pawn =>
    pawn.state == PawnState.Normal || pawn.state == PawnState.Finish

}
