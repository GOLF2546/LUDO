import {  initializeGame } from "./script/script.js";
import { home } from "./script/navigation.js";

document.addEventListener("DOMContentLoaded", () => {
  initializeGame();
  document.querySelector(".home-btn").addEventListener("click", () => {
    home();
  });
});
