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

  

  // val handleExistingPawn: (Player, Int, List[Player]) => Player =
  //   (player, diceValue, allPlayers) => {
  //     val pawnsThatCanMove = PlayerFunctions.getPawnsThatCanMove(player)
  //     val pawnsAtSatrt = PlayerFunctions.getPawnsAtStart(player)

  //     if (pawnsThatCanMove.nonEmpty || diceValue == 6) {
  //       println(
  //         "Your start pawns are: " + pawnsAtSatrt
  //           .map(_.PawnId)
  //           .mkString(", ")
  //       )
  //       println(
  //         "Your movable pawns are: " + pawnsThatCanMove
  //           .map(_.PawnId)
  //           .mkString(", ")
  //       )
  //       println("Which pawn do you want to move?")

  //       val choice = StdIn.readLine("Enter the pawn ID: ").toIntOption

  //       choice match {
  //         case Some(pawnId) if pawnsThatCanMove.exists(_.PawnId == pawnId) =>
  //           val otherPlayerPawns =
  //             allPlayers.filter(_.id != player.id).flatMap(_.pawns)
  //           PlayerFunctions.movePawn(
  //             player,
  //             pawnId,
  //             diceValue,
  //             otherPlayerPawns
  //           )
  //         case Some(pawnId)
  //             if pawnsAtSatrt.exists(_.PawnId == pawnId) && diceValue == 6 =>
  //           val otherPlayerPawns =
  //             allPlayers.filter(_.id != player.id).flatMap(_.pawns)
  //           PlayerFunctions.movePawn(
  //             player,
  //             pawnId,
  //             diceValue,
  //             otherPlayerPawns
  //           )
  //         case _ =>
  //           println("Invalid choice or input. Turn skipped.")
  //           player
  //       }
  //     } else {
  //       println("You don't have any pawn that can move. Turn skipped.")
  //       player
  //     }

  //   }

  // val rollDiceAndWaitForInput: (Player, List[Player]) => Player =
  //   (player, allPlayers) => {
  //     val diceValue = dice()
  //     println(s"You rolled a $diceValue!")
  //     handleExistingPawn(player, diceValue, allPlayers)
  //   }

  // val cheatcode: (Player, List[Player], Int) => Player =
  //   (player, allPlayers, cheat) => {
  //     println(s"You cheat a $cheat!")
  //     handleExistingPawn(player, cheat, allPlayers)
  //   }
  // val playTurn: GameState => GameState = gameState => {
  //   val currentPlayer = gameState.players(gameState.currentPlayerIndex)

  //   if (currentPlayer.id == 1) {
  //     val gameStateStr = GameStatePrinter.createGameStateStr(gameState)
  //     println(gameStateStr)
  //   }

  //   println(s"\nPlayer ${currentPlayer.id}'s turn (${currentPlayer.color})")
  //   println("1. Roll the dice normally")
  //   println("2. Use a cheat code")

  //   StdIn.readLine("Enter your choice (1 or 2): ") match {
  //     case "1" =>
  //       println("Press Enter to roll the dice...")
  //       StdIn.readLine()
  //       val updatedPlayer =
  //         rollDiceAndWaitForInput(currentPlayer, gameState.players)

  //       val updatedPlayers =
  //         gameState.players.updated(gameState.currentPlayerIndex, updatedPlayer)
  //       GameState(updatedPlayers, gameState.currentPlayerIndex)

  //     case "2" =>
  //       println("Enter your cheat code (dice value): ")
  //       StdIn.readLine().toIntOption match {
  //         case Some(cheatValue) if cheatValue >= 1 && cheatValue <= 6 =>
  //           val updatedPlayer =
  //             cheatcode(currentPlayer, gameState.players, cheatValue)
  //           val updatedPlayers = gameState.players.updated(
  //             gameState.currentPlayerIndex,
  //             updatedPlayer
  //           )
  //           GameState(updatedPlayers, gameState.currentPlayerIndex)

  //         case _ =>
  //           println("Invalid cheat code. Turn skipped.")
  //           gameState
  //       }

  //     case _ =>
  //       println("Invalid choice. Turn skipped.")
  //       gameState
  //   }
  // }


  // val gameLoop: GameState => Unit = gameState => {
  //   val updatedState = playTurn(gameState)

  //   println("\nPress Enter for next turn or type 'quit' to end the game")
  //   StdIn.readLine().toLowerCase match {
  //     case "quit" =>
  //       println("Game ended.")
  //     case _ =>
  //       gameLoop(
  //         GameState(
  //           updatedState.players,
  //           (updatedState.currentPlayerIndex + 1) % updatedState.players.length
  //         )
  //       )
  //   }
  // }

  // gameLoop(GameState(createInitialPlayers(), 0))
}
