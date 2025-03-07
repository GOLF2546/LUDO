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
              "PawnId" -> pawn.PawnId, // ✅ Add PawnId
              "initialX" -> pawn.initialX,
              "state" -> pawn.state
            )
          }
        )
      }
      val jsonResponse = Json.toJson(formattedPlayers)
      println(s"DEBUG: startGame response - $jsonResponse")
      Ok(jsonResponse)
    } else {
      InternalServerError("Failed to load players")
    }
  }

  def handleGameClick: Action[JsValue] = Action(parse.json) { request =>
    // Extract dice value and color from the request body
    val diceValue = (request.body \ "diceValue").as[Int]
    val color = (request.body \ "color").as[String].toLowerCase
    val pawnId = (request.body \ "pawnId").as[Int]

    // Load the current game state
    val gameState = loadGameState()
    val players = gameState.players

    // Find the current player based on the currentPlayerIndex
    val currentPlayer = players(gameState.currentPlayerIndex)

    println(
      s"DEBUG: Received handleGameClick - diceValue=$diceValue, color=$color, pawnId=$pawnId, Players=$players"
    )
    println(s"DEBUG: Players loaded: ${players.map(_.id).mkString(", ")}")
    println(s"DEBUG: Current player index: ${gameState.currentPlayerIndex}")
    println(s"DEBUG: Current player: ${currentPlayer}")

    if (currentPlayer.color.toString.toLowerCase == color) {
      // Print player details for debugging
      println(
        s"DEBUG: Player found - ID: ${currentPlayer.id}, Color: ${currentPlayer.color}, Pawn ID: $pawnId"
      )

      // Call the playGame method with the player details
      val updatedGameState = Board.playGame(gameState, pawnId, diceValue)

      // Save the updated game state
      saveGameState(updatedGameState)

      // Return the updated player as a response
      val updatedPlayer =
        updatedGameState.players.find(_.id == currentPlayer.id).get
      Ok(Json.toJson(updatedPlayer))
    } else {
      // If no player is found, return a BadRequest
      println("ERROR: Player not found in the current players list.")
      println(
        s"DEBUG: Expected color: $color, Current player color: ${currentPlayer.color}"
      )
      BadRequest("Player not found")
    }
  }

  def restartGame: Action[AnyContent] = Action {
    // Delete the existing game state file to reset
    val file = new File(gameStateFile)
    if (file.exists()) {
      file.delete()
    }

    // Create initial players and game state
    val players = Board.createInitialPlayers()
    val gameState = GameState(players, 1)
    saveGameState(gameState)

    // Format the players for the response
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
    println(s"DEBUG: restartGame response - $jsonResponse")
    Ok(jsonResponse)
  }
  private def loadGameState(): GameState = {
    val file = new File(gameStateFile)
    if (file.exists()) {
      val jsonString = Source.fromFile(gameStateFile).getLines().mkString
      Json.parse(jsonString).as[GameState]
    } else {
      println("Game state file does not exist, creating initial players.")
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
