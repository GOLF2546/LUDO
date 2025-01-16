package models

case class Player(id: Int, color: Color, pawns: List[Pawn])

object PlayerFunctions {
  import PawnFunctions._
  
  
  // val getColor: Player => (Color) = Player => 
  //   (Player.color)
  
  val showPawns: Player => String = player => 
    player.pawns.mkString("\n")

  val movePawn: (Player, Int, Int, List[Pawn]) => Player = (player, pawnId, steps, otherPawns) => {
    val updatedPawns = player.pawns.map { pawn =>
      if (pawn.PawnId == pawnId) {
        val (newX, newY) = PawnFunctions.move(pawn, steps, otherPawns)
        pawn.copy(initialX = newX, initialY = newY)
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

  val movePawnToStartPosition: Player => Player = player => {
    findStartingPawnWithLeastId(player) match {
      case Some(pawnId) =>
        val updatedPawns = player.pawns.map { pawn =>
          if (pawn.PawnId == pawnId) {
            pawn.copy(initialX = 1, initialY = 1)
          } else pawn
        }
        println(s"Move $pawnId to start position")
        player.copy(pawns = updatedPawns)
      case None =>
        println("None to move pawn to start position")
        player
    }
  }

  val getPawnsThatCanMove: Player => List[Pawn] = player => 
    player.pawns.filterNot(PawnFunctions.isPawnAtStart)
}
