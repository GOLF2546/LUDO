async function restart() {
  try {
    await fetch("/restartGame", {
      method: "GET",
      headers: { Accept: "application/json" },
    });
  } catch (error) {
    console.error("Error initializing game:", error);
  }
}
export { restart };
