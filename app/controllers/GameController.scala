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
    val players = loadGameState()
    if (players.nonEmpty) {
      val formattedPlayers = players.map { player =>
        // Start with the ID first
        val playerData: Seq[(String, JsValue)] =
          Seq("id" -> JsNumber(player.id)) ++
            player.pawns.sortBy(_.PawnId).map { pawn =>
              s"initialX Pawn${pawn.PawnId}" -> JsNumber(pawn.initialX)
            }

        JsObject(playerData) // Convert list of tuples to JsObject
      }

      Ok(Json.toJson(formattedPlayers))
    } else {
      InternalServerError("Failed to load players")
    }
  }

  def handlePawnClick: Action[JsValue] = Action(parse.json) { request =>
    // Extract dice value and color from the request body
    val diceValue = (request.body \ "diceValue").as[Int]
    val color = (request.body \ "color").as[String]
    val pawnId = (request.body \ "pawnId").as[Int]

    // Load the current game state (players list)
    val players = loadGameState()

    // Find the current player based on the color
    val currentPlayer = players.find(_.color.toString == color)

    println(
      s"DEBUG: Received handlePawnClick - diceValue=$diceValue, color=$color, pawnId=$pawnId ,Players=$players"
    )
    println(s"DEBUG: Players loaded: ${players.map(_.id).mkString(", ")}")

    // Ensure that Board.handlePawnClick is accessible and not null
    // println(
    //   s"DEBUG: Checking if Board.handlePawnClick is null: ${Board.handlePawnClick == null}"
    // )

    currentPlayer match {
      case Some(player) =>
        // Print player details for debugging
        println(
          s"DEBUG: Player found - ID: ${player}, Color: ${player.color}, Pawn ID: $pawnId"
        )

        // Call the handlePawnClick method with the player details before checking for null
        println("you in this case?")
        val updatedPlayer =
          Board.handlePawnClick(player, pawnId, diceValue, players)

        // If handlePawnClick is null, log and return an error
        if (updatedPlayer == null) {
          println("ERROR: Board.handlePawnClick returned null!")
          InternalServerError("Error in processing pawn move.")
        } else {
          // Save the updated game state
          saveGameState(players.map {
            case p if p.id == updatedPlayer.id => updatedPlayer
            case p                             => p
          })

          // Return the updated player as a response
          Ok(Json.toJson(updatedPlayer))
        }

      case None =>
        // If no player is found, return a BadRequest
        println("ERROR: Player not found in the current players list.")
        BadRequest("Player not found")
    }
  }

  // Helper function to load the game state from the JSON file
  private def loadGameState(): List[Player] = {
    val file = new File(gameStateFile)
    // println(
    //   s"Trying to load game state from: ${file.getAbsolutePath}"
    // ) // Debugging

    if (file.exists()) {
      val jsonString = Source.fromFile(gameStateFile).getLines().mkString
      println(s"Loaded JSON: $jsonString") // Debugging
      Json.parse(jsonString).as[List[Player]]
    } else {
      println("Game state file does not exist, creating initial players.")
      Board.createInitialPlayers()
    }
  }

  // Helper function to save the game state to the JSON file
  private def saveGameState(players: List[Player]): Unit = {
    val jsonString = Json.toJson(players).toString()
    val writer = new PrintWriter(new File(gameStateFile))
    writer.write(jsonString)
    writer.close()
  }
}
