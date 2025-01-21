package models

import scala.io.StdIn

case class GameState(
    players: List[Player],
    currentPlayerIndex: Int
)

object Board extends App {
  import PlayerFunctions._
  import PawnFunctions._

  val dice: () => Int = () => scala.util.Random.nextInt(6) + 1

  val createInitialPlayers: () => List[Player] = () => {
    val playerColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow)
    (1 to 4).toList.zip(playerColors).map { case (id, color) =>
      Player(
        id,
        color,
        (1 to 4).map(id => Pawn(id, 0, 0, color, PawnState.Start)).toList
      )
    }
  }

  val handleNewPawn: Player => Player = player => {
    PlayerFunctions.movePawnToStartPosition(player)
  }

  val handleExistingPawn: (Player, Int, List[Player]) => Player =
    (player, diceValue, allPlayers) => {
      val pawnsThatCanMove = PlayerFunctions.getPawnsThatCanMove(player)

      if (pawnsThatCanMove.nonEmpty) {
        println(
          "Your movable pawns are: " + pawnsThatCanMove
            .map(_.PawnId)
            .mkString(", ")
        )
        println("Which pawn do you want to move?")

        val choice = StdIn.readLine("Enter the pawn ID: ").toIntOption

        choice match {
          case Some(pawnId) if pawnsThatCanMove.exists(_.PawnId == pawnId) =>
            val otherPlayerPawns =
              allPlayers.filter(_.id != player.id).flatMap(_.pawns)
            PlayerFunctions.movePawn(
              player,
              pawnId,
              diceValue,
              otherPlayerPawns
            )

          case _ =>
            println("Invalid choice or input. Turn skipped.")
            player
        }
      } else {
        println("You don't have any pawn that can move. Turn skipped.")
        player
      }
    }

  val rollDiceAndWaitForInput: (Player, List[Player]) => Player =
    (player, allPlayers) => {
      val diceValue = dice()
      println(s"You rolled a $diceValue!")

      if (diceValue == 6) {
        println("1. Move a pawn from start")
        println("2. Move an existing pawn")

        StdIn.readLine("Enter your choice (1 or 2): ") match {
          case "1" => handleNewPawn(player)
          case "2" => handleExistingPawn(player, diceValue, allPlayers)
          case _ =>
            println("Invalid choice. Turn skipped")
            player
        }
      } else {
        handleExistingPawn(player, diceValue, allPlayers)
      }
    }

  val playTurn: GameState => GameState = gameState => {
    val currentPlayer = gameState.players(gameState.currentPlayerIndex)
    if (currentPlayer.id == 1) {
      val gameStateStr = GameStatePrinter.createGameStateStr(gameState)
      println(gameStateStr)
    }

    println(s"\nPlayer ${currentPlayer.id}'s turn (${currentPlayer.color})")
    println("Press Enter to roll dice...")
    StdIn.readLine()

    val updatedPlayer =
      rollDiceAndWaitForInput(currentPlayer, gameState.players)
    val updatedPlayers =
      gameState.players.updated(gameState.currentPlayerIndex, updatedPlayer)

    GameState(updatedPlayers, gameState.currentPlayerIndex)
  }

  val gameLoop: GameState => Unit = gameState => {
    val updatedState = playTurn(gameState)

    println("\nPress Enter for next turn or type 'quit' to end the game")
    StdIn.readLine().toLowerCase match {
      case "quit" =>
        println("Game ended.")
      case _ =>
        gameLoop(
          GameState(
            updatedState.players,
            (updatedState.currentPlayerIndex + 1) % updatedState.players.length
          )
        )
    }
  }

  gameLoop(GameState(createInitialPlayers(), 0))
}
