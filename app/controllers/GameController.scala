package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models._
import scala.util.{Try, Success, Failure}
@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


def startGame: Action[AnyContent] = Action {
  val players = Board.createInitialPlayers()
  println(players)  // Debug log to check what is returned
  Either.cond(
    players.nonEmpty,
    Json.toJson(players),
    InternalServerError("Failed to initialize players")
  ).fold(identity, Ok(_))
}



}
