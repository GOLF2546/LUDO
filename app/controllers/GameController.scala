package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class GameController @Inject()(
    val controllerComponents: ControllerComponents,
    diceController: DiceController
) extends BaseController {

def startGame = Action { implicit request =>
  val diceResult = diceController.rollDice()
  Redirect(routes.GameController.showGame()).flashing("diceResult" -> diceResult.toString)
}

def showGame = Action { implicit request =>
  Ok(views.html.game()) // Render the game page
}

}

