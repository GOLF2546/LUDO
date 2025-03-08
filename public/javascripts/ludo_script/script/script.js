let diceValue = 0;
let playerTurn = 0;

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
    updatePlayerPositions(playersData.players);
    playerTurn = playersData.turn;

    // Display simple message instead of raw JSON
    document.getElementById("dice-result").innerText =
      "Game initialized. Roll the dice to begin!" +
      " Player " +
      playerTurn +
      " turn";
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}

async function selectPawn(playerId, color, pawnId) {
  console.log(
    `DEBUG: selectPawn called with color=${color}, playerId=${playerId}, pawnId=${pawnId}`
  ); // Debugging

  if (!pawnId) {
    console.error("Pawn ID is missing!");
    alert("Error: Pawn ID is missing.");
    return;
  }

  if (diceValue === 0) {
    alert("You must roll the dice first!");
    return;
  }

  const payload = {
    pawnId: pawnId, // ✅ Ensure pawnId is included
    color: color,
    diceValue: diceValue,
  };

  console.log("Sending payload:", payload); // Debugging

  try {
    const response = await fetch("/handleGameClick", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Server error: ${response.status} - ${errorText}`);
    }

    const updatedPlayer = await response.json();
    console.log("Player updated:", updatedPlayer);

    // Debugging: Print the updated player data
    console.log("DEBUG: updatedPlayer data:", updatedPlayer);

    // Update the UI
    updatePlayerPositions(updatedPlayer);
    await initializeGame();
  } catch (error) {
    console.error("Error handling pawn click:", error);
    alert("Failed to move the pawn. Please try again.");
  }
}

function placePawnOnBoard(cellId, playerId, color, pawnId, state) {
  const cell = document.getElementById(cellId);
  if (cell) {
    const pawn = document.createElement("div");
    pawn.id = `player-${playerId}-pawn-${pawnId}`;
    pawn.className = `${color}-pawn`;

    const imagePath = `/assets/images/components/pawn/${color.charAt(0)}.png`;
    pawn.style.backgroundImage = `url('${imagePath}')`;
    pawn.style.backgroundSize = "contain";
    pawn.style.backgroundRepeat = "no-repeat";
    pawn.style.backgroundPosition = "center";

    pawn.onclick = () => selectPawn(playerId, color, pawnId);

    if (state === "End") {
      pawn.style.position = "absolute";
      pawn.style.width = "25%";
      pawn.style.height = "25%";

      switch (color) {
        case "Red":
          pawn.style.bottom = "0";
          pawn.style.left = "50%";
          pawn.style.transform = "translateX(-50%)";
          break;
        case "Green":
          pawn.style.top = "50%";
          pawn.style.left = "0";
          pawn.style.transform = "translateY(-50%)";
          break;
        case "Yellow":
          pawn.style.top = "0";
          pawn.style.left = "50%";
          pawn.style.transform = "translateX(-50%)";
          break;
        case "Blue":
          pawn.style.top = "50%";
          pawn.style.right = "0";
          pawn.style.transform = "translateY(-50%)";
          break;
      }
    } else {
      pawn.style.width = "80%";
      pawn.style.height = "80%";
      pawn.style.borderRadius = "50%";
      pawn.style.margin = "auto";
    }

    cell.appendChild(pawn);
  } else {
    console.error(`❌ Cell with ID ${cellId} not found!`);
  }
}

function clearBoardPawn() {
  const pathSquares = document.querySelectorAll(".path-square");
  pathSquares.forEach((square) => {
    // Remove all pawn elements (with any color)
    const pawns = square.querySelectorAll("[class$='-pawn']");
    pawns.forEach((pawn) => pawn.remove());
  });

  // Clear center star positions
  const centerPositions = ["Y6", "B6", "G6", "R6"];
  centerPositions.forEach((id) => {
    const element = document.getElementById(id);
    if (element) {
      // Clear all pawns from center positions
      const pawns = element.querySelectorAll("[class$='-pawn']");
      pawns.forEach((pawn) => pawn.remove());
    }
  });
}

function updatePlayerPositions(playersData) {
  clearBoardPawn();

  const colorMap = {
    1: "Green",
    2: "Yellow",
    3: "Blue",
    4: "Red",
  };

  const playersArray = Array.isArray(playersData) ? playersData : [playersData];

  playersArray.forEach((player) => {
    const playerId = player.id;
    const color = colorMap[playerId];

    player.pawns.forEach((pawn, pawnIndex) => {
      console.log("🔍 Checking pawn data:", pawn);

      if (!pawn.PawnId) {
        console.error("❌ Error: Pawn ID is missing in player data!", pawn);
        return;
      }

      const pawnId = pawn.PawnId;
      const cellId = pawn.initialX ? pawn.initialX.toString() : null;

      if (pawn.state === "Start") {
        const homeSquare = document.querySelector(
          `.home-base-${color.toLowerCase()} .home-square`
        );
        if (homeSquare) {
          const pawnElement = createPawnElement(playerId, color, pawnId, pawn.state);
          homeSquare.appendChild(pawnElement);
        }
      } else if (pawn.state === "Normal" && cellId) {
        placePawnOnBoard(cellId, playerId, color, pawnId, pawn.state);
      } else if (pawn.state === "Finish") {
        placePawnOnBoard(
          `${color.charAt(0).toUpperCase()}${pawn.initialX}`,
          playerId,
          color,
          pawnId,
          pawn.state
        );
      } else if (pawn.state === "End") {
        placePawnOnBoard(
          `${color.charAt(0).toUpperCase()}6`,
          playerId,
          color,
          pawnId,
          pawn.state
        );
      }
    });
  });
}

function createPawnElement(playerId, color, pawnId, state) {
  const pawnElement = document.createElement("img");
  pawnElement.src = `/assets/images/components/pawn/${color.charAt(0)}.png`;
  pawnElement.alt = `${color} pawn`;
  pawnElement.classList.add("pawn");
  pawnElement.dataset.pawnId = pawnId;
  pawnElement.dataset.state = state;
  return pawnElement;
}

export { rollDice, initializeGame };
