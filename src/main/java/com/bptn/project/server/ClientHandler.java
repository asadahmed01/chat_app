package com.bptn.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader ;
    private BufferedWriter bufferedWriter;
    private String userName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            // Handle username registration
            handleUsernameRegistration();

            // Main message loop
            String messageFromClient;
            while (socket.isConnected() && (messageFromClient = bufferedReader.readLine()) != null) {
                handleClientMessage(messageFromClient);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }


    private void handleUsernameRegistration() throws IOException {
        while (true) {
            userName = bufferedReader.readLine();
            if (userName == null || userName.trim().isEmpty()) {
                sendMessage("ERROR: Username cannot be empty");
                continue;
            }

            if (isUsernameUnique(userName)) {
                sendMessage("SUCCESS: Username registered successfully");
                clientHandlers.add(this);
                broadcastMessage("SERVER: " + userName + " has joined the chat");
                sendOnlineUsers();
                break;
            } else {
                sendMessage("ERROR: Username already taken");
            }
        }
    }

    private void handleClientMessage(String message) {
        if (message.startsWith("/")) {
            handleCommand(message);
        } else {
            broadcastMessage(userName + ": " + message);
        }
    }


    private void handleCommand(String command) {
        switch (command.toLowerCase()) {
            case "/users":
                sendOnlineUsers();
                break;
            case "/help":
                sendHelpMessage();
                break;
            case "/quit":
                closeEverything();
                break;
            default:
                sendMessage("Unknown command. Type /help for available commands");
        }
    }


    private void sendHelpMessage() {
        String helpMessage = "\nAvailable commands:\n" +
                "/users - Show online users\n" +
                "/help  - Show this help message\n" +
                "/quit  - Exit the chat\n";
        sendMessage(helpMessage);
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clientHandlers) {
            if (!client.userName.equals(this.userName)) {
                client.sendMessage(message);
            }
        }
    }


    private void sendOnlineUsers() {
        StringBuilder users = new StringBuilder("\nOnline Users:\n");
        for (ClientHandler client : clientHandlers) {
            users.append("- ").append(client.userName).append("\n");
        }
        sendMessage(users.toString());
    }


    private void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + userName + " has left the chat");
    }

    public String showOnlineUser(){
        StringBuilder onlineUsers = new StringBuilder("[");

        for (ClientHandler clientHandler : clientHandlers) {
            onlineUsers.append(userName).append(", ");
        }

        if (onlineUsers.length() > 1) {
            onlineUsers.setLength(onlineUsers.length() - 2);  // Remove the last comma and space
        }

        onlineUsers.append("]");  // Close the list with a closing bracket
        return onlineUsers.toString();
    }


    private static boolean isUsernameUnique(String username) {
        return clientHandlers.stream().noneMatch(client -> client.userName.equals(username));
    }

    private void closeEverything() {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
