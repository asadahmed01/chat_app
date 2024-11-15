package com.bptn.project.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private boolean isRunning = true;
    String RESET = "\u001B[0m";
    String GREEN = "\u001B[32m";


    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything();
        }
    }


    public void start() {
        try {
            // Register username first of all
            registerUsername();

            // Start message listener thread
            new Thread(this::listenForMessages).start();

            // Handle user input in main thread
            handleUserInput();
        } catch (IOException e) {
            //if there is an error, close socket and buffer reader and writer
            closeEverything();
        }
    }



    private void registerUsername() throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean isRegistered = false;
        //keep asking for valid username until unique one is registered
        while (!isRegistered) {
            System.out.print("\nEnter your username: ");
            userName = scanner.nextLine().trim();

            if (userName.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }

            // Send username to server for verification
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Get server response
            String response = bufferedReader.readLine();
            //if server responds with success, registeration is complete
            if (response.startsWith("SUCCESS")) {
                isRegistered = true;
                System.out.println(GREEN + "\nWelcome to the chat room, " + userName +  RESET);

                showMenu();
            } else {
                //display the error message from server
                System.out.println(response);
            }
        }
    }


    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning && socket.isConnected()) {
            try {
                String message = scanner.nextLine();
                //listen for the quit command
                if (message.equalsIgnoreCase("/quit")) {
                    isRunning = false;
                    System.out.println("Leaving chat...");
                    bufferedWriter.write("/quit");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    closeEverything();
                    break;
                }

                sendMessage(message);
                System.out.println("You: " + message);
            } catch (IOException e) {
                //whene there is error, close socket and buffers and break out of the loop
                closeEverything();
                break;
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }


    private void listenForMessages() {
        //while socket is running, keep listening for messages from other clients
        while (isRunning) {
            try {
                String message = bufferedReader.readLine();
                if (message == null){
                    break;
                }
                System.out.println(message);
            } catch (IOException e) {
                if (isRunning) {
                    System.out.println("Lost connection to server.");
                }
                closeEverything();
                break;
            }
        }
    }

    private void showMenu() {
        //display available commands to the user
        System.out.println("\n=== Chat Room Commands ===");
        System.out.println("/users - Show online users");
        System.out.println("/menu  - Show menu");
        System.out.println("/quit  - Exit the chat");
        System.out.println("Start typing to send messages!");
        System.out.println("========================\n");
    }


    private void closeEverything() {
        isRunning = false;
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    public static void main(String[] args) {
        try {
            System.out.println("Connecting to chat server...");
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connected successfully!");

            Client client = new Client(socket);
            client.start();
        }catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
