async function restart() {
  try {
    const response = await fetch("/restartGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const playersData = await response.json();
    updatePlayerPositions(playersData);

    // Display simple message instead of raw JSON
    document.getElementById("dice-result").innerText =
      "Game initialized. Roll the dice to begin!";
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}
export { restart };