async function startGame() {
  try {
    await fetch("/startGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
  } catch (error) {
    console.error("Error starting game:", error);
  }
}
export { startGame };
