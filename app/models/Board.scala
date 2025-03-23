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

  val isCanStart: (GameState, Int) => GameState =
    (gameState, diceValue) => {
      val player = gameState.players(gameState.currentPlayerIndex)
      if (diceValue != 6) {
        if (player.pawns.forall(_.state == PawnState.Start)) {
          val nextPlayerIndex =
            (gameState.currentPlayerIndex + 1) % gameState.players.length
          gameState.copy(currentPlayerIndex = nextPlayerIndex)
        } else {
          gameState
        }
      } else {
        gameState
      }
    }

  def movePawnAndUpdatePlayers(
      player: Player,
      pawnId: Int,
      diceValue: Int,
      otherPlayers: List[Player]
  ): (Player, List[Player]) = {
    val otherPlayerPawns = otherPlayers.flatMap(_.pawns)
    val (movedPlayer, updatedOtherPawns) =
      PlayerFunctions.movePawn(player, pawnId, diceValue, otherPlayerPawns)

    val updatedOtherPlayers = otherPlayers.map { otherPlayer =>
      if (updatedOtherPawns.exists(_.color == otherPlayer.color)) {
        otherPlayer.copy(
          pawns = otherPlayer.pawns.map { p =>
            updatedOtherPawns
              .find(up => up.PawnId == p.PawnId && up.color == p.color)
              .getOrElse(p)
          }
        )
      } else otherPlayer
    }
    (movedPlayer, updatedOtherPlayers)
  }

  val playGame: (GameState, Int, Int) => GameState =
    (gameState, pawnId, diceValue) => {
      val player = gameState.players(gameState.currentPlayerIndex)
      val otherPlayers = gameState.players.filter(_.id != player.id)
      val pawnOption = player.pawns.find(_.PawnId == pawnId)

      val (updatedCurrentPlayer, updatedOtherPlayers, isChangedPlayer) =
        pawnOption match {
          case Some(pawn)
              if PawnFunctions.isPawnCanMove(pawn) || diceValue == 6 =>
            val (movedPlayer, updatedOtherPawns) =
              movePawnAndUpdatePlayers(player, pawnId, diceValue, otherPlayers)
            (movedPlayer, updatedOtherPawns, !(diceValue == 6))

          case Some(pawn)
              if PawnFunctions.isPawnAtStart(pawn) && diceValue == 6 =>
            val (movedPlayer, updatedOtherPawns) =
              movePawnAndUpdatePlayers(player, pawnId, diceValue, otherPlayers)
            (movedPlayer, updatedOtherPawns, false)

          case _ =>
            (player, otherPlayers, true)
        }

      val updatedPlayers = gameState.players.map { p =>
        if (p.color == updatedCurrentPlayer.color) updatedCurrentPlayer
        else updatedOtherPlayers.find(_.color == p.color).getOrElse(p)
      }
      val nextPlayerIndex =
        if (isChangedPlayer)
          (gameState.currentPlayerIndex + 1) % gameState.players.length
        else gameState.currentPlayerIndex
      GameState(updatedPlayers, nextPlayerIndex)
    }

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
