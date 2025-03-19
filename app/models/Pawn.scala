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
      case Color.Blue   => (27) // Blue pawns start at (0, 0)
      case Color.Red    => (40) // Red pawns start at (13, 0)
      case Color.Green  => (1) // Green pawns start at (26, 0)
      case Color.Yellow => (14) // Yellow pawns start at (39, 0)

    }
  }

  val getPosition: Pawn => (Int, PawnState) = pawn =>
    (pawn.initialX, pawn.state)

  val move: (Pawn, Int, List[Pawn]) => (Int, Int, PawnState, List[Pawn]) =
    (pawn, steps, otherPawns) => {
      val newY = pawn.initialY + steps
      val (newX, newState) = (pawn.state, newY) match {
        case (PawnState.Start, y) if y == 6 =>
          (setPosition(pawn.color), PawnState.Normal)

        case (PawnState.Normal, y) if y > 56 =>
          val nextX = newY - 56
          (nextX, PawnState.Finish)

        case (PawnState.Finish, y) =>
          // Calculate position in the final stretch (color[0]1 through color[0]6)
          val colorPrefix = pawn.color match {
            case Color.Blue   => "B"
            case Color.Red    => "R"
            case Color.Green  => "G"
            case Color.Yellow => "Y"
          }
          // The position in the final stretch (starts at 1, ends at 6)
          val finalPosition =
            y - 56 // Adjust this calculation based on your game mechanics

          if (finalPosition >= 6) {
            (0, PawnState.End)
          } else {
            (finalPosition, PawnState.Finish)
          }

        case _ =>
          val nextX = (pawn.initialX + steps) match {
              case x if x > 52 => x - 52
              case x => x
            }
          (nextX, pawn.state)
      }

      // Perform position chec
      val updateOtherPawn = checkPosition(newX, newState, otherPawns)

      // Return the new position and state
      (newX, newY, newState,updateOtherPawn)
    }
  val checkPosition: (Int, PawnState, List[Pawn]) => List[Pawn] =
    (newX, state, otherPawns) => {
      val updatedPawns = otherPawns.map { pawn =>
        if (
          pawn.state == PawnState.Normal && getPosition(pawn) == (newX, state)
        ) {
          pawn.copy(initialX = 0, initialY = 0, state = PawnState.Start) // Create a new Pawn with the updated state
        } else {
          pawn // Keep the original Pawn unchanged
        }
      }
      updatedPawns
    }

  val isPawnAtStart: Pawn => Boolean = pawn =>
    // pawn.initialX == 0 &&
    pawn.state == PawnState.Start

  val isPawnCanMove: Pawn => Boolean = pawn =>
    pawn.state == PawnState.Normal || pawn.state == PawnState.Finish

}
