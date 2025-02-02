package com.example.mafia;

public class Message {
    private String username;
    private String message;
    private Integer clientProfileLogo;

    public Message(String username, String message, Integer clientProfileLogo) {
        this.username = username;
        this.message = message;
        this.clientProfileLogo = clientProfileLogo;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Integer getClientProfileLogo() {return clientProfileLogo; }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}