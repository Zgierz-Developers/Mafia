const express = require('express');
const http = require('http');
const socketIO = require('socket.io');
const cors = require('cors');
const path = require('path');

const app = express();
const server = http.createServer(app);

// Configure Socket.IO
const io = socketIO(server, {
  cors: {
    origin: "*",  // Allow all origins, adjust this if needed for security
    methods: ["GET", "POST"],
    credentials: true
  }
});

const PORT = process.env.PORT || 8080;

// Middleware
app.use(cors());  // Enable CORS for cross-origin requests

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// In-memory storage for rooms
const rooms = {};

// Socket.IO Connection Event
io.on('connection', (socket) => {
  console.log('New client connected');

  // Handle creating a room
  socket.on('createRoom', ({ roomName, ownerName }) => {
    if (rooms[roomName]) {
      socket.emit('error', { message: 'Room already exists' });
    } else {
      rooms[roomName] = { owner: ownerName, players: [ownerName], hostSocketId: socket.id };
      socket.join(roomName);
      console.log(`Room created: ${roomName} by ${ownerName}`);
      io.emit('roomList', rooms); // Update all clients with the new room list
    }
  });

  // Handle joining a room
  socket.on('joinRoom', ({ roomName, playerName }) => {
    if (rooms[roomName]) {
      rooms[roomName].players.push(playerName);
      socket.join(roomName);
      console.log(`${playerName} joined room: ${roomName}`);
      io.to(roomName).emit('playerJoined', { playerName });
      io.emit('roomList', rooms); // Update all clients with the updated room list
    } else {
      socket.emit('error', { message: 'Room does not exist' });
    }
  });

  // Handle listing rooms
  socket.on('listRooms', () => {
    socket.emit('roomList', rooms);
  });

  // Handle sending a message in a room
  socket.on('sendMessage', (data) => {
    console.log(`Message from ${data.username}: ${data.message}`);
    socket.to(data.gameCode).emit('message', data);
    console.log(`Message sent to room ${data.gameCode}`);
  });

  // Send a welcome message to the client
  socket.emit('serverMessage', { message: 'Welcome to the game!' });

  // Handle disconnection
  socket.on('disconnect', () => {
    console.log('Client disconnected');

    // Check if the disconnecting client is the host of any room
    for (const roomName in rooms) {
      if (rooms[roomName].hostSocketId === socket.id) {
        console.log(`Host of room ${roomName} disconnected.`);
        if (rooms[roomName].players.length > 1) {
          // Promote a new host
          rooms[roomName].players = rooms[roomName].players.filter(player => player !== rooms[roomName].owner);
          const newHost = rooms[roomName].players[0];
          rooms[roomName].owner = newHost;
          rooms[roomName].hostSocketId = Object.keys(io.sockets.sockets).find(id => io.sockets.sockets[id].username === newHost);
          console.log(`New host of room ${roomName} is ${newHost}`);
          io.to(roomName).emit('newHost', { newHost });
        } else {
          // Delete the room if it becomes empty
          console.log(`Deleting room ${roomName}`);
          delete rooms[roomName];
        }
        io.emit('roomList', rooms); // Update all clients with the updated room list
        break;
      }
    }
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));
