const express = require('express');
const http = require('http');
const socketIO = require('socket.io');
const cors = require('cors');
const path = require('path');

const app = express();
const server = http.createServer(app);

// Configure Socket.IO to use polling as the transport method for better compatibility
const io = socketIO(server, {
  cors: {
    origin: "*",  // Allow all origins, adjust this if needed for security
    methods: ["GET", "POST"],
    credentials: true
  },
  transports: ['polling'] // Use polling to ensure compatibility
});

const PORT = process.env.PORT || 8080;

// Middleware
app.use(cors());  // Enable CORS for cross-origin requests

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// Socket.IO Connection Event
io.on('connection', (socket) => {
  console.log('New client connected');

  // Handle joining a game
  socket.on('joinGame', (gameCode) => {
    socket.join(gameCode);
    console.log(`Client joined game: ${gameCode}`);

    // Notify other players in the game about the new player
    socket.to(gameCode).emit('playerJoined');
  });

  // Handle sending a message in a game
  socket.on('sendMessage', (data) => {
    console.log(`Message from ${data.username}: ${data.message}`);
    socket.to(data.gameCode).emit('message', data);
  });

  // Send a welcome message to the client
  socket.emit('serverMessage', { message: 'Welcome to the game!' });

  // Handle disconnection
  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));
