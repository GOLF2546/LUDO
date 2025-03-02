// Dice value state
let diceValue = 0;

// Navigation function
function homePage() {
  window.location.href = "/"; // Adjust if needed
}

// Function to roll the dice and display the result
async function rollDice() {
  try {
    const response = await fetch("/dice/roll", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const result = await response.json();
    diceValue = result;
    document.getElementById("dice-result").innerText = "Dice Roll: " + result;
  } catch (error) {
    console.error("Error rolling dice:", error);
    document.getElementById("dice-result").innerText = "Failed to roll the dice.";
  }
}

// Initialize game function
async function initializeGame() {
  try {
    const response = await fetch("/game/start", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const playersData = await response.json();

    updatePlayerPositions(playersData);
    document.getElementById("dice-result").innerText = "Game initialized. Roll the dice to begin!";
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}

// Update player positions on the board
function updatePlayerPositions(playersData) {
  clearBoardPawn();

  const colorMap = { 1: "red", 2: "blue", 3: "green", 4: "yellow" };

  playersData.forEach(player => {
    const color = colorMap[player.id];

    player.pawns.forEach((pawn, pawnIndex) => {
      const pawnId = `${color}-pawn-${pawnIndex}`;

      if (pawn.state === "Normal") {
        const cellId = pawn.initialX.toString();
        if (cellId) placePawnOnBoard(cellId, color, pawnId);
      } else if (pawn.state === "End") {
        placePawnOnBoard(`${color.charAt(0).toUpperCase()}6`, color, pawnId);
      }
    });
  });
}

// Clear board pawns
function clearBoardPawn() {
  document.querySelectorAll('.path-square .pawn').forEach(pawn => pawn.remove());

  ["Y6", "B6", "G6", "R6"].forEach(id => {
    const element = document.getElementById(id);
    if (element) element.innerHTML = '';
  });
}

// Place a pawn on the board
function placePawnOnBoard(cellId, color, pawnId) {
  const cell = document.getElementById(cellId);
  if (cell) {
    const pawn = document.createElement('div');
    pawn.id = pawnId;
    pawn.className = `pawn ${color}-pawn`;
    Object.assign(pawn.style, {
      width: "80%",
      height: "80%",
      borderRadius: "50%",
      margin: "auto",
      backgroundColor: color === "green" ? "#4CAF50" :
                      color === "yellow" ? "#FFEB3B" :
                      color === "blue" ? "#2196F3" :
                      "#F44336"
    });

    cell.appendChild(pawn);
  }
}

// Auto-initialize when DOM is loaded
document.addEventListener("DOMContentLoaded", initializeGame);
