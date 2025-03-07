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
  def restartGame: Action[AnyContent] = Action {
    val players = Board.createInitialPlayers()
    saveGameState(GameState(players, 1))
    Ok("Game restarted")
  }
def handleGameClick: Action[JsValue] = Action(parse.json) { request =>
  // Extract dice value and color from the request body
  val diceValue = (request.body \ "diceValue").as[Int]
  val color = (request.body \ "color").as[String]
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

  if (currentPlayer.color.toString == color) {
    // Print player details for debugging
    println(
      s"DEBUG: Player found - ID: ${currentPlayer.id}, Color: ${currentPlayer.color}, Pawn ID: $pawnId"
    )

    // Call the playGame method with the player details
    val updatedGameState = Board.playGame(gameState, pawnId, diceValue)

    // Save the updated game state
    saveGameState(updatedGameState)

    // Return the updated player as a response
    val updatedPlayer = updatedGameState.players.find(_.id == currentPlayer.id).get
    Ok(Json.toJson(updatedPlayer))
  } else {
    // If no player is found, return a BadRequest
    println("ERROR: Player not found in the current players list.")
    BadRequest("Player not found")
  }
}
  // def handlePawnClick: Action[JsValue] = Action(parse.json) { request =>
  //   // Extract dice value and color from the request body
  //   val diceValue = (request.body \ "diceValue").as[Int]
  //   val color = (request.body \ "color").as[String]
  //   val pawnId = (request.body \ "pawnId").as[Int]

  //   // Load the current game state
  //   val gameState = loadGameState()
  //   val players = gameState.players

  //   // Find the current player based on the color
  //   val currentPlayer = players.find(_.color.toString == color)

  //   println(
  //     s"DEBUG: Received handlePawnClick - diceValue=$diceValue, color=$color, pawnId=$pawnId ,Players=$players"
  //   )
  //   println(s"DEBUG: Players loaded: ${players.map(_.id).mkString(", ")}")

  //   currentPlayer match {
  //     case Some(player) =>
  //       // Print player details for debugging
  //       println(
  //         s"DEBUG: Player found - ID: ${player.id}, Color: ${player.color}, Pawn ID: $pawnId"
  //       )

  //       // Call the handlePawnClick method with the player details
  //       val updatedPlayer =
  //         Board.handlePawnClick(player, pawnId, diceValue, players)

  //       // Save the updated game state
  //       saveGameState(
  //         GameState(
  //           players.map {
  //             case p if p.id == updatedPlayer.id => updatedPlayer
  //             case p                             => p
  //           },
  //           gameState.currentPlayerIndex
  //         )
  //       )

  //       // Return the updated player as a response
  //       Ok(Json.toJson(updatedPlayer))

  //     case None =>
  //       // If no player is found, return a BadRequest
  //       println("ERROR: Player not found in the current players list.")
  //       BadRequest("Player not found")
  //   }
  // }

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
