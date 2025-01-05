const express = require('express');
const http = require('http');
const socketIO = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIO(server);

const PORT = process.env.PORT || 8080; // Zmieniono port na 8080

app.use(express.static(__dirname + '/public'));

io.on('connection', (socket) => {
  console.log('New client connected');

  socket.on('joinGame', (gameCode) => {
    socket.join(gameCode);
    console.log(`Client joined game: ${gameCode}`);

    // Powiadom innych graczy w grze o nowym graczu
    socket.to(gameCode).emit('playerJoined');
  });

  socket.on('sendMessage', (data) => {
    // Wyślij wiadomość do wszystkich graczy w grze
    socket.to(data.gameCode).emit('message', data);
  });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));