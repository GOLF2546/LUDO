package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

@Singleton
class DiceController @Inject()(val controllerComponents: ControllerComponents, gameController: GameController) extends BaseController {

  def rollDice = Action {
    val diceRoll = scala.util.Random.nextInt(6) + 1 // Generate a random number from 1 to 6
    val (playerTurn, isChanged) = gameController.checkPawnMove(diceRoll)
    val responseJson = Json.obj(
      "diceValue" -> diceRoll,
      "gameState" -> playerTurn,
      "isChanged" -> isChanged
    )
    Ok(responseJson) // Return the updated game state, dice value, and change status as JSON
  }

}