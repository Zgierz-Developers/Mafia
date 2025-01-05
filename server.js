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

io.on('connection', (socket) => {
  console.log('New client connected');

  socket.on('joinGame', (gameCode) => {
    socket.join(gameCode);
    console.log(`Client joined game: ${gameCode}`);

    // Notify other players in the game about the new player
    socket.to(gameCode).emit('playerJoined');
  });

  socket.on('sendMessage', (data) => {
    // Log the received message
    console.log(`Message from ${data.username}: ${data.message}`);

    // Send message to all players in the game
    socket.to(data.gameCode).emit('message', data);
  });

  // Emit a message to the client
  socket.emit('serverMessage', { message: 'Welcome to the game!' });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));