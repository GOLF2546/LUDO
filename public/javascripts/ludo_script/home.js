import { startGamePage, howTo} from "./script/navigation.js";
import { startGame } from "./script/hook/startgame.js";
import { restart } from "./script/hook/restartgame.js";

document.addEventListener("DOMContentLoaded", () => {
  document.querySelector(".new-game-btn").addEventListener("click", () => {
    startGamePage();
    restart();
  });

  document.querySelector(".continue-btn").addEventListener("click", () => {
    startGamePage();
    startGame();
  });

  document.querySelector(".how-to-btn").addEventListener("click", () => {
    howTo();
  });
});
