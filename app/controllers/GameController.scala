package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.util.Random
import java.io.{File, PrintWriter}
import scala.io.Source
import models._

@Singleton
class GameController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {

  private val gameStateFile = "gameState.json"

  import play.api.libs.json._
  import play.api.mvc._

  def startGame: Action[AnyContent] = Action {
    val gameState = loadGameState()
    val players = gameState.players
    if (players.nonEmpty) {
      val formattedPlayers = players.map { player =>
        Json.obj(
          "id" -> player.id,
          "pawns" -> player.pawns.sortBy(_.PawnId).map { pawn =>
            Json.obj(
              "PawnId" -> pawn.PawnId,
              "initialX" -> pawn.initialX,
              "state" -> pawn.state
            )
          }
        )
      }
      val jsonResponse = Json.obj(
        "players" -> formattedPlayers,
        "turn" -> gameState.currentPlayerIndex
      )
      Ok(jsonResponse)
    } else {
      InternalServerError("Failed to load players")
    }
  }

  def handleGameClick: Action[JsValue] = Action(parse.json) { request =>
    val diceValue = (request.body \ "diceValue").as[Int]
    val color = (request.body \ "color").as[String].toLowerCase
    val pawnId = (request.body \ "pawnId").as[Int]
    val gameState = loadGameState()
    val players = gameState.players
    val currentPlayer = players(gameState.currentPlayerIndex)
    if (currentPlayer.color.toString.toLowerCase == color) {
      val updatedGameState = Board.playGame(gameState, pawnId, diceValue)
      saveGameState(updatedGameState)
      val updatedPlayer =
        updatedGameState.players.find(_.id == currentPlayer.id).get
      Ok(Json.toJson(updatedPlayer))
    } else {
      BadRequest("Player not found")
    }
  }

  def restartGame: Action[AnyContent] = Action {
    val file = new File(gameStateFile)
    if (file.exists()) {
      file.delete()
    }
    val players = Board.createInitialPlayers()
    val gameState = GameState(players, 0)
    saveGameState(gameState)
    val formattedPlayers = players.map { player =>
      Json.obj(
        "id" -> player.id,
        "pawns" -> player.pawns.sortBy(_.PawnId).map { pawn =>
          Json.obj(
            "PawnId" -> pawn.PawnId,
            "initialX" -> pawn.initialX,
            "state" -> pawn.state
          )
        }
      )
    }
    val jsonResponse = Json.toJson(formattedPlayers)
    Ok(jsonResponse)
  }
  
  private def loadGameState(): GameState = {
    val file = new File(gameStateFile)
    if (file.exists()) {
      val jsonString = Source.fromFile(gameStateFile).getLines().mkString
      Json.parse(jsonString).as[GameState]
    } else {
      val players = Board.createInitialPlayers()
      val gameState = GameState(players, 1)
      saveGameState(gameState)
      gameState
    }
  }

  private def saveGameState(gameState: GameState): Unit = {
    val jsonString = Json.toJson(gameState).toString()
    val writer = new PrintWriter(new File(gameStateFile))
    writer.write(jsonString)
    writer.close()
  }
}
