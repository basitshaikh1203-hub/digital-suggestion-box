const express = require("express");

const app = express();
const PORT = 5000;
// Database setup branch
app.use(express.json());

app.get("/", (req, res) => {
  res.json({
    message: "Digital Suggestion Box backend is running!",
  });
});

app.listen(PORT, () => {
  console.log(`Backend server running on http://localhost:${PORT}`);
});