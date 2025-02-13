import axios from "axios";
import { Player } from "../models/models";


// Function to start the game
export const startGame = async (): Promise<void> => {
  try {
    await axios.post("/startGame");
  } catch (error) {
    console.error("Error starting game", error);
  }
};

// Function to fetch players from the backend
export const fetchPlayers = async (): Promise<Player[]> => {
  try {
    const response = await axios.get<Player[]>("/players");
    return response.data;
  } catch (error) {
    console.error("Error fetching players", error);
    return [];
  }
};
