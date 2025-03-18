package models

import play.api.libs.json._

case class Player(id: Int, color: Color, pawns: List[Pawn])

object Player {
  implicit val playerFormat: Format[Player] = Json.format[Player]
}

object PlayerFunctions {
  import PawnFunctions._

  val showPawns: Player => String = player => player.pawns.mkString("\n")

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

  val findStartingPawnWithLeastId: Player => Option[Int] = player => {
    player.pawns
      .filter(PawnFunctions.isPawnAtStart)
      .map(_.PawnId)
      .sorted
      .headOption
  }

  val getPawnsThatCanMove: Player => List[Pawn] = player =>
    player.pawns.filter(PawnFunctions.isPawnCanMove)

  val getPawnsAtStart: Player => List[Pawn] = player =>
    player.pawns.filter(PawnFunctions.isPawnAtStart)

}
