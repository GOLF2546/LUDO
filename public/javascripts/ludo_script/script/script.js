import { restart } from "./hook/restartgame.js";
import { startGamePage } from "./navigation.js";
let diceValue = 0;
let playerTurn = 0;
async function rollDice() {
  if (diceValue !== 0) {
    alert(`You already rolled the dice and the value is: ${diceValue}`);
    return;
  }
  try {
    const response = await fetch("/rollDice", {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    });

    const result = await response.json();
    diceValue = result.diceValue;
    const playerTurn = result.playerTurn;
    const isChanged = result.isChanged;
    const diceButton = document.querySelector(".roll-btn");

    if (diceButton) {
      // Smooth transition effect for dice update
      diceButton.src = `/assets/images/components/dice/${diceValue}.png`;
      diceButton.alt = `Dice showing ${diceValue}`;
    }

    if (isChanged) {
      const newDiceButton = createDiceButton(playerTurn, 0);
      diceButton.replaceWith(newDiceButton);

      startGamePage(); // Smoothly restart the game state
    }
  } catch (error) {
    console.error("Error rolling dice:", error);
  }
}

async function initializeGame() {
  try {
    const response = await fetch("/startGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
    const playersData = await response.json();
    updatePlayerPositions(playersData.players);
    playerTurn = playersData.turn;

    let diceValue = 0;
    const rollButton = document.querySelector(".roll-btn");
    if (rollButton) {
      const diceElement = createDiceButton(playerTurn, diceValue);
      rollButton.replaceWith(diceElement);
    }
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}

async function selectPawn(color, pawnId) {
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
    pawnId: pawnId,
    color: color,
    diceValue: diceValue,
  };

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
    updatePlayerPositions(updatedPlayer);
    diceValue = 0;
    await initializeGame();
  } catch (error) {
    alert("Failed to move the pawn. Please try again.");
  }
}

function placePawnOnBoard(cellId, playerId, color, pawnId, state, numOfPawn) {
  const cell = document.getElementById(cellId);
  console.log(cellId);
  if (cell) {
    const pawn = document.createElement("div");
    pawn.id = `player-${playerId}-pawn-${pawnId[0]}`;
    pawn.className = `${color}-pawn`;

    const imagePath = `/assets/images/components/pawn/${
      color.charAt(0) + numOfPawn
    }.png`;
    pawn.style.backgroundImage = `url('${imagePath}')`;
    pawn.style.backgroundSize = "contain";
    pawn.style.backgroundRepeat = "no-repeat";
    pawn.style.backgroundPosition = "center";

    pawn.onclick = () => selectPawn(color, pawnId[0]);
    console.log(state);
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
    const pawns = square.querySelectorAll("[class$='-pawn']");
    pawns.forEach((pawn) => pawn.remove());
  });

  const centerPositions = ["Y6", "B6", "G6", "R6"];
  centerPositions.forEach((id) => {
    const element = document.getElementById(id);
    if (element) {
      const pawns = element.querySelectorAll("[class$='-pawn']");
      pawns.forEach((pawn) => pawn.remove());
    }
  });

  const homeSquares = document.querySelectorAll(".home-square");
  homeSquares.forEach((square) => {
    const pawns = square.querySelectorAll(".pawn");
    pawns.forEach((pawn) => pawn.remove());
  });
}
function updatePlayerPositions(playersData) {
  clearBoardPawn();

  const colorMap = { 1: "Green", 2: "Yellow", 3: "Blue", 4: "Red" };
  const playersArray = Array.isArray(playersData) ? playersData : [playersData];

  const cellMap = {}; // Store pawn groups by cell position

  // Group pawns by cell and state
  playersArray.forEach((player) => {
    const playerId = player.id;
    const color = colorMap[playerId];
    player.pawns.forEach((pawn) => {
      if (!pawn.PawnId) {
        return;
      }

      if (pawn.state === "Start") {
        const homeSquare = document.querySelector(
          `.home-base-${color.toLowerCase()} .home-square`
        );
        if (homeSquare) {
          const pawnElement = createPawnElement(color, pawn.PawnId, pawn.state);
          homeSquare.appendChild(pawnElement);
        }
      } else {
        const cellId = pawn.initialX ? pawn.initialX.toString() : "end";
        const state = pawn.state;
        const key = `${cellId}-${state}-${color}`;

        if (!cellMap[key]) {
          cellMap[key] = { pawnIds: [], playerId, color, cellId, state };
        }
        cellMap[key].pawnIds.push(pawn.PawnId);
      }
    });
  });
  Object.values(cellMap).forEach(
    ({ pawnIds, playerId, color, cellId, state }) => {
      if (!cellId) return;

      const len = pawnIds.length.toString(); // Number of pawns in this cell

      switch (state) {
        case "Normal":
          placePawnOnBoard(cellId, playerId, color, pawnIds, state, len);
          break;

        case "Finish":
          placePawnOnBoard(
            `${color.charAt(0).toUpperCase()}${cellId}`,
            playerId,
            color,
            pawnIds,
            state,
            len
          );
          break;

        case "End":
          const endCellId = `${color.charAt(0).toUpperCase()}6`; // Should match 'R6', 'G6', 'B6', 'Y6'
          placePawnOnBoard(endCellId, playerId, color, pawnIds, state, len);
          break;
      }
    }
  );

  // Check for winners
  playersArray.forEach((player) => {
    const color = colorMap[player.id];
    const endPawnCount = player.pawns.filter(
      (pawn) => pawn.state === "End"
    ).length;
    if (endPawnCount === 4) CheckWinner(color, endPawnCount);
  });
}

function createDiceButton(playerTurn, diceValue) {
  const diceButton = document.createElement("img");
  diceButton.src = `/assets/images/components/dice/${diceValue}.png`;
  diceButton.alt = `Dice showing ${diceValue}`;
  diceButton.classList.add("roll-btn");
  diceButton.style.cursor = "pointer";
  diceButton.style.position = "absolute";

  switch (playerTurn) {
    case 0:
      diceButton.style.top = "35px";
      diceButton.style.left = "200px";
      break;
    case 1:
      diceButton.style.top = "35px";
      diceButton.style.right = "200px";
      break;
    case 2:
      diceButton.style.bottom = "35px";
      diceButton.style.right = "200px";
      break;
    case 3:
      diceButton.style.bottom = "35px";
      diceButton.style.left = "200px";
      break;
  }

  diceButton.addEventListener("click", rollDice);

  return diceButton;
}

function createPawnElement(color, pawnId, state) {
  const pawnElement = document.createElement("img");
  pawnElement.src = `/assets/images/components/pawn/${color.charAt(0)}.png`;
  pawnElement.alt = `${color} pawn`;
  pawnElement.classList.add("pawn");
  pawnElement.dataset.pawnId = pawnId;
  pawnElement.dataset.state = state;
  pawnElement.style.cursor = "pointer";
  pawnElement.onclick = () => selectPawn(color, pawnId);

  return pawnElement;
}
function CheckWinner(color, numEndPawn) {
  if (numEndPawn == 4) {
    const existingWinnerImage = document.querySelector(".winner-image");
    if (existingWinnerImage) {
      return;
    }
    const imagePath = `/assets/images/components/winner/${color.charAt(0)}.png`;
    const winnerImage = document.createElement("img");
    winnerImage.src = imagePath;
    winnerImage.alt = `${color} wins!`;
    winnerImage.classList.add("winner-image");

    const winnerContainer = document.getElementById("winner-container");
    const gameBoard = document.getElementById("game-board");
    if (winnerContainer && gameBoard) {
      winnerContainer.appendChild(winnerImage);

      // Add NG.png image for restarting the game
      const restartImage = document.createElement("img");
      restartImage.src = `/assets/images/components/winner/NG.png`;
      restartImage.alt = "Restart Game";
      restartImage.classList.add("restart-image");
      restartImage.style.cursor = "pointer";
      restartImage.style.position = "absolute";
      restartImage.style.bottom = "10px";
      restartImage.style.right = "10px";
      restartImage.addEventListener("click", () => {
        startGamePage();
        restart();
      });

      winnerContainer.style.position = "relative";
      winnerContainer.appendChild(restartImage);
      winnerContainer.style.display = "block";
      gameBoard.style.display = "none";
    } else {
      console.error("Winner container or game board not found!");
    }
  }
}

export { rollDice, initializeGame };
