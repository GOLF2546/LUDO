import { home } from "./script/navigation.js";

document.addEventListener("DOMContentLoaded", () => {
  document.querySelector(".home-btn").addEventListener("click", () => {
    home();
  });
});