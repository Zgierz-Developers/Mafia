const express = require("express");
const http = require("http");
const socketIO = require("socket.io");
const cors = require("cors");
const path = require("path");
const { profile } = require("console");

const app = express();
const server = http.createServer(app);

// Configure Socket.IO
const io = socketIO(server, {
  cors: {
    allowEIO3: true, // Enables compatibility with Socket.IO v2.x clients
    origin: "*", // Allow all origins, adjust this if needed for security
    methods: ["GET", "POST"],
    credentials: true,
  },
});

const PORT = process.env.PORT || 8080;

// Middleware
app.use(cors()); // Enable CORS for cross-origin requests

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, "public")));

// In-memory storage for rooms
const rooms = {};

// Socket.IO Connection Event
io.on("connection", (socket) => {
  console.log("New client connected");

  // Handle creating a room
  socket.on("createRoom", ({ roomName, ownerName }) => {
    if (rooms[roomName]) {
      socket.emit("error", { message: "Room already exists" });
    } else {
      rooms[roomName] = {
        owner: ownerName,
        players: [ownerName],
        hostSocketId: socket.id,
      };
      socket.join(roomName);
      socket.username = ownerName; // Store the owner's username in the socket object
      console.log(`Room created: ${roomName} by ${ownerName}`);
      io.emit("roomList", rooms); // Update all clients with the new room list
      io.to(roomName).emit("message", {
        username: "System",
        message: `${ownerName} created the room.`,
      });
    }
  });

  // Handle joining a room
  socket.on("joinRoom", ({ roomName, playerName, selectedAvatar }) => {
    if (rooms[roomName]) {
      if (!rooms[roomName].players.includes(playerName)) {
        rooms[roomName].players.push(playerName);
      }
      socket.join(roomName);
      socket.username = playerName; // Store the player's username in the socket object
      socket.selectedAvatar = selectedAvatar; // Store the player's client profile logo in the socket object
      console.log(
        `${playerName} joined room: ${roomName} with client profile logo: ${selectedAvatar}`
      );
      io.to(roomName).emit("playerJoined", { playerName });
      io.emit("roomList", rooms); // Update all clients with the updated room list
      io.to(roomName).emit("message", {
        username: "System",
        message: `${playerName} joined the room.`,
        profile_pic: selectedAvatar,
      });
    } else {
      socket.emit("error", { message: "Room does not exist" });
    }
  });

  // Handle listing rooms
  socket.on("listRooms", () => {
    socket.emit("roomList", rooms);
  });

  // Handle sending a message in a room
  socket.on("sendMessage", (data) => {
    console.log(`Message from ${data.username}: ${data.message}, profile_pic: ${data.selectedAvatar}, data: ${data}`);
    socket.to(data.gameCode).emit("message", data);
    console.log(`Message sent to room ${data.gameCode}`);
  });

  // Handle leaving a room
  socket.on("leaveRoom", ({ roomName, username }) => {
    if (rooms[roomName]) {
      const room = rooms[roomName];
      const playerIndex = room.players.indexOf(username);

      if (playerIndex !== -1) {
        room.players.splice(playerIndex, 1);
        console.log(`${username} left room: ${roomName}`);
        io.to(roomName).emit("message", {
          username: "System",
          message: `${username} left the room.`,
        });

        if (room.hostSocketId === socket.id) {
          console.log(`Host of room ${roomName} left.`);
          if (room.players.length > 0) {
            const newHost = room.players[0];
            room.owner = newHost;
            room.hostSocketId = Object.keys(io.sockets.sockets).find(
              (id) => io.sockets.sockets[id].username === newHost
            );
            console.log(`New host of room ${roomName} is ${newHost}`);
            io.to(roomName).emit("newHost", { newHost });
            io.to(roomName).emit("message", {
              username: "System",
              message: `${newHost} is the new host.`,
            });
          } else {
            console.log(`Deleting room ${roomName}`);
            delete rooms[roomName];
          }
        }

        io.emit("roomList", rooms);
      }
    }
  });

  // Send a welcome message to the client
  socket.emit("serverMessage", { message: "Welcome to the game!" });

  // Handle disconnection
  socket.on("disconnect", () => {
    console.log("Client disconnected: " + socket.username);
    // Check if the disconnecting client is in any room
    for (const roomName in rooms) {
      const room = rooms[roomName];
      const playerIndex = room.players.indexOf(socket.username);

      if (playerIndex !== -1) {
        // Remove the player from the room
        const playerName = room.players.splice(playerIndex, 1)[0];
        console.log(`${playerName} left room: ${roomName}`);
        io.to(roomName).emit("message", {
          username: "System",
          message: `${playerName} left the room.`,
        });

        if (room.hostSocketId === socket.id) {
          console.log(`Host of room ${roomName} disconnected.`);
          if (room.players.length > 0) {
            // Promote a new host
            const newHost = room.players[0];
            room.owner = newHost;
            room.hostSocketId = Object.keys(io.sockets.sockets).find(
              (id) => io.sockets.sockets[id].username === newHost
            );
            console.log(`New host of room ${roomName} is ${newHost}`);
            io.to(roomName).emit("newHost", { newHost });
            io.to(roomName).emit("message", {
              username: "System",
              message: `${newHost} is the new host.`,
            });
          } else {
            // Delete the room if it becomes empty
            console.log(`Deleting room ${roomName}`);
            delete rooms[roomName];
          }
        }

        io.emit("roomList", rooms); // Update all clients with the updated room list
        break;
      }
    }
  });
});

server.listen(PORT, () => console.log(`Server listening on port ${PORT}`));
