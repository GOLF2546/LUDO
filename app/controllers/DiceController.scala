package controllers

import javax.inject._
import play.api._
import play.api.mvc._


@Singleton
class DiceController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
    def rollDice = Action {
      val dice: () => Int = () => scala.util.Random.nextInt(6) + 1
      Ok(dice().toString) // Return the random dice roll as a string
    }
}
