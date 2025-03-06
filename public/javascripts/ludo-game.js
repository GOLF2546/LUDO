// Dice value state
let diceValue = 0;

// Navigation function
function startGame() {
  window.location.href = '/start';
}

function home() {
  window.location.href = "/";
}

function howTo() {
  window.location.href = "/howTo";
}

// Function to roll the dice and display the result
async function rollDice() {
  try {
    const response = await fetch("/rollDice", {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    });
    const result = await response.json();
    diceValue = result;
    document.getElementById("dice-result").innerText = "Dice Roll: " + result;
  } catch (error) {
    console.error("Error rolling dice:", error);
    document.getElementById("dice-result").innerText =
      "Failed to roll the dice.";
  }
}

async function initializeGame() {
  try {
    const response = await fetch("/startGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const playersData = await response.json();

    // Update player positions based on data
    updatePlayerPositions(playersData);

    // Display simple message instead of raw JSON
    document.getElementById("dice-result").innerText =
      "Game initialized. Roll the dice to begin!";
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}

function updatePlayerPositions(playersData) {
  clearBoardPawn();

  const colorMap = {
    1: "red",
    2: "blue",
    3: "green",
    4: "yellow",
  };

  playersData.forEach((player) => {
    const playerId = player.id;
    const color = colorMap[playerId];

    // Process each pawn for this player
    player.pawns.forEach((pawn, pawnIndex) => {
      const pawnId = `${color}-pawn-${pawnIndex}`;

      if (pawn.state === "Start") {
        // Pawn is in home base - nothing to do as they're rendered by the server template
      } else if (pawn.state === "Normal") {
        // Pawn is on the board at a specific position
        const cellId = pawn.initialX.toString();
        if (cellId) {
          placePawnOnBoard(cellId, color, pawnId, pawn.state);
        }
      } else if (pawn.state === "End") {
        // Pawn has reached the end (could place in center or finish area)
        // For now, we'll place it in the center
        placePawnOnBoard(
          `${color.charAt(0).toUpperCase()}6`,
          color,
          pawnId,
          pawn.state
        );
      }
    });
  });
}

function clearBoardPawn() {
  const pathSquares = document.querySelectorAll(".path-square");
  pathSquares.forEach((square) => {
    // Remove pawn elements but keep the square itself
    const pawns = square.querySelectorAll(".pawn");
    pawns.forEach((pawn) => pawn.remove());
  });

  // Clear center star positions
  const centerPositions = ["Y6", "B6", "G6", "R6"];
  centerPositions.forEach((id) => {
    const element = document.getElementById(id);
    if (element) element.innerHTML = "";
  });
}

function placePawnOnBoard(cellId, color, pawnId, state) {
  const cell = document.getElementById(cellId);
  if (cell) {
    const pawn = document.createElement("div");
    pawn.id = pawnId;
    pawn.className = `${color}-pawn`;

    // Set the background image
    const imagePath = `/assets/images/components/pawn/${color.charAt(0)}.png`;
    pawn.style.backgroundImage = `url('${imagePath}')`;
    pawn.style.backgroundSize = "contain";
    pawn.style.backgroundRepeat = "no-repeat";
    pawn.style.backgroundPosition = "center";

    if (state === "End") {
      // Set absolute positioning inside the center-star
      pawn.style.position = "absolute";
      pawn.style.width = "25%";
      pawn.style.height = "25%";

      // Adjust placement based on color
      switch (color) {
        case "red":
          pawn.style.bottom = "0";
          pawn.style.left = "50%";
          pawn.style.transform = "translateX(-50%)";
          break;
        case "green":
          pawn.style.top = "50%";
          pawn.style.left = "0";
          pawn.style.transform = "translateY(-50%)";
          break;
        case "yellow":
          pawn.style.top = "0";
          pawn.style.left = "50%";
          pawn.style.transform = "translateX(-50%)";
          break;
        case "blue":
          pawn.style.top = "50%";
          pawn.style.right = "0";
          pawn.style.transform = "translateY(-50%)";
          break;
      }
    } else {
      // Normal and Start: Full size, centered
      pawn.style.width = "80%";
      pawn.style.height = "80%";
      pawn.style.borderRadius = "50%";
      pawn.style.margin = "auto";
    }

    // Append pawn to the cell
    cell.appendChild(pawn);
  }
}

// Auto-initialization when DOM content is loaded
document.addEventListener("DOMContentLoaded", initializeGame);
