package com.bptn.project.client;

import com.bptn.project.server.ClientHandler;

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


    public Client(String userName, Socket socket){
        try {
            this.userName = userName;
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void sendMessage(){
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(userName +": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("(You): " + message);
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();  // Read message from the server
                        System.out.println(msgFromGroupChat);           // Display message on the console
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);  // Handle exceptions
                    }
                }
            }
        }).start();
    }







    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();  // Close the reader if it is not null
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();  // Close the writer if it is not null
            }
            if (socket != null) {
                socket.close();  // Close the socket if it is not null
            }
        } catch (IOException e) {
            e.printStackTrace();  // Print the exception stack trace if an error occurs
        }
    }
    





    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");

        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(username, socket);
        // Start listening for messages in a new thread
        System.out.println("Online users [" + ClientHandler.clientHandlers.size() + "]");
        client.listenForMessage();
        // Start sending messages in the main thread
        client.sendMessage();

    }
}
