package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

@Singleton
class DiceController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def rollDice = Action {
    val diceRoll = scala.util.Random.nextInt(6) + 1 // Generate a random number from 1 to 6
    Ok(Json.toJson(diceRoll)) // Return the number as JSON
  }

}
