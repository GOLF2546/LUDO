package models

// import play.api.libs.json._

case class Player(id: Int, color: Color, pawns: List[Pawn])

// object Player {
//   implicit val playerFormat: Format[Player] = Json.format[Player]
// }

object PlayerFunctions {
  import PawnFunctions._

  // val getColor: Player => (Color) = Player =>
  //   (Player.color)

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
  // val movePawnToStartPosition: (Player, Int, Int, List[Pawn]) => Player =
  //   (player, pawnId, steps, otherPawns) => {
  //     val updatedPawns = player.pawns.map { pawn =>
  //       if (pawn.PawnId == pawnId) {
  //         val (newX, newY, newState) =
  //           PawnFunctions.move(pawn, steps, otherPawns)
  //         pawn.copy(initialX = newX, initialY = newY, state = newState)
  //       } else pawn
  //     }
  //     println(s"Move $pawnId to start position")
  //     player.copy(pawns = updatedPawns)
  //   }
  // val movePawnToStartPosition: Player => Player = player => {
  //   findStartingPawnWithLeastId(player) match {
  //     case Some(pawnId) =>
  //       val updatedPawns = player.pawns.map { pawn =>
  //         if (pawn.PawnId == pawnId) {
  //           pawn.copy(
  //             initialX = setPosition(player.color),
  //             initialY = 1,
  //             state = PawnState.Normal
  //           )
  //         } else pawn
  //       }
  //       println(s"Move $pawnId to start position")
  //       player.copy(pawns = updatedPawns)
  //     case None =>
  //       println("None to move pawn to start position")
  //       player
  //   }
  // }
//   val movePawnToStartPosition: (Player, Int) => Player = (player, pawnId) => {
//   player.pawns.find(pawn => pawn.PawnId == pawnId && pawn.state == PawnState.Start) match {
//     case Some(_) =>
//       val updatedPawns = player.pawns.map { pawn =>
//         if (pawn.PawnId == pawnId) {
//           pawn.copy(
//             initialX = setPosition(player.color),
//             initialY = 1,
//             state = PawnState.Normal
//           )
//         } else pawn
//       }
//       println(s"Pawn $pawnId moved to start position")
//       player.copy(pawns = updatedPawns)

//     case None =>
//       println(s"Pawn $pawnId is not in start state or doesn't exist")
//       player
//   }
// }


  // val getPawnsThatCanMove: Player => List[Pawn] = player =>
  //   player.pawns.filterNot(PawnFunctions.isPawnAtStart)


