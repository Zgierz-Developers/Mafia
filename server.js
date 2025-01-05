const express = require('express');
const http = require('http');
const socketIO = require('socket.io');
const cors = require('cors');

const app = express();
const server = http.createServer(app);
const io = socketIO(server, {
  cors: {
    origin: "*", // Allow all origins for testing purposes
    methods: ["GET", "POST"]
  }
});

const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(express.static(__dirname + '/public'));

// Add a route for the root path
app.get('/', (req, res) => {
  res.send('Server is running!');
});

app.get('/test', (req, res) => {
  res.send('Server is running!');
});

io.on('connection', (socket) => {
  console.log('New client connected');

  socket.on('joinGame', (gameCode) => {
    socket.join(gameCode);
    console.log(`Client joined game: ${gameCode}`);

    // Notify other players in the game about the new player
    socket.to(gameCode).emit('playerJoined');
  });

  socket.on('sendMessage', (data) => {
    // Send message to all players in the game
    socket.to(data.gameCode).emit('message', data);
  });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));