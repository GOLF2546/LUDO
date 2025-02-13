package controllers

import javax.inject._
import play.api.mvc._
import models._
import play.api.libs.json._
import scala.util.Random

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // JSON formatters for serialization
  implicit val pawnStateWrites: Writes[PawnState] = Writes {
    case PawnState.Start  => JsString("Start")
    case PawnState.Normal => JsString("Normal")
    case PawnState.Finish => JsString("Finish")
    case PawnState.End    => JsString("End")
  }
  implicit val colorWrites: Writes[Color] = Writes { color =>
    JsString(color.toString) // Convert Color to a JSON string
  }

  implicit val pawnWrites: OWrites[Pawn] = Json.writes[Pawn]
  implicit val playerWrites: OWrites[Player] = Json.writes[Player]

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
  // Start the game (returning initialized players instead of mutating state)
private var players: List[Player] = createInitialPlayers()

// Add the createPlayers action to your controller
def createPlayers = Action {
  players = createInitialPlayers() // Reset players when creating them
  Ok(Json.toJson(players)) // Return players as JSON
}


def getPlayers = Action {
  Ok(Json.toJson(players)) // Return the current state of players as JSON
}


}