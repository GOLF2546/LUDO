async function startGame() {
  try {
    const response = await fetch("/startGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const gameState = await response.json();
    const players = gameState.players;

    // Clear existing pawns from the board
    clearBoardPawn();

    // Place each pawn on the board based on its position
    players.forEach((player) => {
      console.log(`DEBUG: Processing player with color=${player.color}`); // Debugging
      player.pawns.forEach((pawn) => {
        const cellId = pawn.initialX.toString(); // Assuming initialX is the cell ID
        placePawnOnBoard(
          cellId,
          player.id,
          player.color,
          pawn.PawnId,
          pawn.state
        );
      });
    });

    document.getElementById("game-players").innerHTML = playerDetails;
  } catch (error) {
    console.error("Error starting game:", error);
    document.getElementById("game-players").innerText =
      "Failed to start the game.";
  }
}
export { startGame };