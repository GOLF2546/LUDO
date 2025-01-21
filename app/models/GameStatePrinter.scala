package models
import PlayerFunctions._
import PawnFunctions._

object GameStatePrinter {

  // val createPawnStateStr: (Pawn, Color) => String = (pawn, color) => {
  //   val (x, y) = setPosition(pawn, color) // Use setPosition with Pawn and Color
  //   val atStart = PawnFunctions.isPawnAtStart(pawn) // Check if the pawn is at its starting position
  //   val status = if (atStart) "at start" else "in play"
  //   s"Pawn ${pawn.PawnId}: Position($x, $y) - $status - Color(${color})"
  // }

  // val createPlayerStateStr: Player => String = player => {
  //   val pawnsStr = player.pawns
  //     .map(pawn => createPawnStateStr(pawn, player.color)) // Pass both Pawn and Player's Color
  //     .mkString("\n")
  //   s"\nPlayer ${player.id} (${player.color}) pawns:\n$pawnsStr"
  // }
  val createPawnStateStr: Pawn => String = pawn => {
    val (x, y) = PawnFunctions.getPosition(pawn)
    val canMove = PawnFunctions.isPawnCanMove(pawn)
    // val atStart = PawnFunctions.isPawnAtStart(pawn)
    val status = if (canMove) "can't move" else "in play"
    s"Pawn ${pawn.PawnId}: Position($x, $y) - $status"
  }

  val createPlayerStateStr: Player => String = player => {
    val pawnsStr = player.pawns
      .map(createPawnStateStr)
      .mkString("\n")
    s"\nPlayer ${player.id} (${player.color}) pawns:\n$pawnsStr"
  }
  val createGameStateStr: GameState => String = gameState => {
    val playersStr = gameState.players
      .map(createPlayerStateStr)
      .mkString("\n")
    s"\n=== Current Game State ===\n$playersStr\n\n======================"
  }
}
