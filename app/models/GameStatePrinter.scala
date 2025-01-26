package models
import PlayerFunctions._
import PawnFunctions._

object GameStatePrinter {
  
  val createPawnStateStr: Pawn => String = pawn => {
    val (x, y) = PawnFunctions.getPosition(pawn)
    val canMove = PawnFunctions.isPawnCanMove(pawn)
    val status = if (canMove) "can move" else "in play"
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
