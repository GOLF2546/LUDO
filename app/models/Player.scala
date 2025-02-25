package models


case class Player(id: Int, color: Color, pawns: List[Pawn])

object PlayerFunctions {
  import PawnFunctions._

  val showPawns: Player => String = player => player.pawns.mkString("\n")

  val movePawn: (Player, Int, Int, List[Pawn]) => Player =
    (player, pawnId, steps, otherPawns) => {
      val updatedPawns = player.pawns.map { pawn =>
        if (pawn.PawnId == pawnId) {
          val (newX, newY, newState) =
            PawnFunctions.move(pawn, steps, otherPawns)
          pawn.copy(initialX = newX, initialY = newY, state = newState)
        } else pawn
      }
      player.copy(pawns = updatedPawns)
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
