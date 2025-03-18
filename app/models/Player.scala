package models

import play.api.libs.json._

case class Player(id: Int, color: Color, pawns: List[Pawn])

object Player {
  implicit val playerFormat: Format[Player] = Json.format[Player]
}

object PlayerFunctions {
  import PawnFunctions._

  val movePawn: (Player, Int, Int, List[Pawn]) => (Player, List[Pawn]) =
    (player, pawnId, steps, otherPawns) => {

      val (updatedPawns, updatedOtherPawns) =
        player.pawns.foldLeft((List.empty[Pawn], otherPawns)) {
          case ((accPawns, accOtherPawns), pawn) =>
            if (pawn.PawnId == pawnId) {
              val (newX, newY, newState, newOtherPawns) =
                PawnFunctions.move(pawn, steps, accOtherPawns)

              (
                accPawns :+ pawn.copy(
                  initialX = newX,
                  initialY = newY,
                  state = newState
                ),
                newOtherPawns
              )
            } else {
              (accPawns :+ pawn, accOtherPawns)
            }
        }

      (player.copy(pawns = updatedPawns), updatedOtherPawns)
    }
}
