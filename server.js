const express = require('express');
const http = require('http');
const socketIO = require('socket.io');
const cors = require('cors');

const app = express();
const server = http.createServer(app);
const io = socketIO(server, {
  cors: {
    origin: "*", // Your frontend URL
    methods: ["GET", "POST"],
    credentials: true // Allow cookies if needed
  },
  transports: ["polling"] // Force polling transport
});

const PORT = process.env.PORT || 8080;

app.use(cors());

app.use(express.static(__dirname + '/public'));

io.on('connection', (socket) => {
  console.log('New client connected');

  socket.on('joinGame', (gameCode) => {
    socket.join(gameCode);
    console.log(`Client joined game: ${gameCode}`);
    socket.to(gameCode).emit('playerJoined', { gameCode });
  });

  socket.on('sendMessage', (data) => {
    console.log(`Message from ${data.username}: ${data.message}`);
    socket.to(data.gameCode).emit('message', data);
  });

  socket.emit('serverMessage', { message: 'Welcome to the game!' });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));
