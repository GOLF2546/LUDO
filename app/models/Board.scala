package models

import scala.io.StdIn
import play.api.libs.json._
import java.io.{File, PrintWriter}
case class GameState(
    players: List[Player],
    currentPlayerIndex: Int
)
object GameState {
  implicit val gameStateFormat: Format[GameState] = Json.format[GameState]
}

object Board {
  import PlayerFunctions._
  import PawnFunctions._
  println("DEBUG: Board object initialized")

val playGame: (GameState, Int, Int) => GameState =
  (gameState, pawnId, diceValue) => {
    val player = gameState.players(gameState.currentPlayerIndex)
    val otherPlayers = gameState.players.filter(_.id != player.id)
    val pawnOption = player.pawns.find(_.PawnId == pawnId)

    println(s"DEBUG: Pawn found: ${pawnOption.map(_.PawnId).getOrElse("None")}")

    val (updatedCurrentPlayer, updatedOtherPlayers) = pawnOption match {
      case Some(pawn) if PawnFunctions.isPawnCanMove(pawn) || diceValue == 6 =>
        println(s"DEBUG: Pawn can move: ${pawn.PawnId}")

        val otherPlayerPawns = otherPlayers.flatMap(_.pawns)
        val (movedPlayer, updatedOtherPawns) =
          PlayerFunctions.movePawn(player, pawnId, diceValue, otherPlayerPawns)

        println(s"DEBUG: Updated Player pawns: ${movedPlayer.pawns.map(_.PawnId).mkString(", ")}")

        // Update other players' pawns based on color matching
        val replacedOtherPlayers = otherPlayers.map { otherPlayer =>
          if (updatedOtherPawns.exists(_.color == otherPlayer.color)) {
            otherPlayer.copy(
              pawns = otherPlayer.pawns.map { p =>
                updatedOtherPawns.find(up => up.PawnId == p.PawnId && up.color == p.color)
                  .getOrElse(p)
              }
            )
          } else otherPlayer
        }

        (movedPlayer, replacedOtherPlayers)

      case Some(pawn) if PawnFunctions.isPawnAtStart(pawn) && diceValue == 6 =>
        println(s"DEBUG: Pawn at start: ${pawn.PawnId}, dice value is 6")

        val otherPlayerPawns = otherPlayers.flatMap(_.pawns)
        val (movedPlayer, updatedOtherPawns) =
          PlayerFunctions.movePawn(player, pawnId, diceValue, otherPlayerPawns)

        println(s"DEBUG: Updated Player pawns: ${movedPlayer.pawns.map(_.PawnId).mkString(", ")}")

        // Update other players' pawns based on color matching
        val replacedOtherPlayers = otherPlayers.map { otherPlayer =>
          if (updatedOtherPawns.exists(_.color == otherPlayer.color)) {
            otherPlayer.copy(
              pawns = otherPlayer.pawns.map { p =>
                updatedOtherPawns.find(up => up.PawnId == p.PawnId && up.color == p.color)
                  .getOrElse(p)
              }
            )
          } else otherPlayer
        }

        (movedPlayer, replacedOtherPlayers)

      case _ =>
        println("DEBUG: Invalid move or no valid pawn to move.")
        (player, otherPlayers)
    }

    // Reconstruct updated player list by replacing the players instead of adding them
    val updatedPlayers = gameState.players.map { p =>
      if (p.color == updatedCurrentPlayer.color) updatedCurrentPlayer
      else updatedOtherPlayers.find(_.color == p.color).getOrElse(p)
    }

    // Increment current player index
    val nextPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.length

    // Return updated game state
    GameState(updatedPlayers, nextPlayerIndex)
  }



  val dice: () => Int = () => scala.util.Random.nextInt(6) + 1
  def createInitialPlayers(): List[Player] = {
    val playerColors = List(Color.Green, Color.Yellow, Color.Blue, Color.Red)
    (1 to 4).toList.zip(playerColors).map { case (id, color) =>
      Player(
        id,
        color,
        (1 to 4).map(id => Pawn(id, 0, 0, color, PawnState.Start)).toList
      )
    }
  }

}
