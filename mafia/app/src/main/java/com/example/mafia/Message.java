package com.example.mafia;

public class Message {
    private String username;
    private String message;
    private Integer clientProfileLogo;
    private Integer clientNickColor;

    public Message(String username, String message, Integer clientProfileLogo, Integer clientNickColor) {
        this.username = username;
        this.message = message;
        this.clientProfileLogo = clientProfileLogo;
        this.clientNickColor = clientNickColor;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Integer getClientProfileLogo() {return clientProfileLogo; }

    public Integer getClientNickColor() {return clientNickColor; }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}