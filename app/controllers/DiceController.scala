package controllers

import javax.inject._
import play.api.mvc._
import scala.util.Random

@Singleton
class DiceController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def rollDice = Action {
    val diceValue = Random.nextInt(6) + 1 // Generates a number between 1 and 6
    Ok(diceValue.toString)
  }
}

