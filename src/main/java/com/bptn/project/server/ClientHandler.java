package com.bptn.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader ;
    private BufferedWriter bufferedWriter;
    private String userName;

    public ClientHandler(Socket socket){
        try {
            
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + userName + "has entered the chat");
        } catch (IOException e) {
            closeSocket(socket, bufferedReader, bufferedWriter);
        }
        
    }

    @Override
    public void run() {
        String message;
        while(socket.isConnected()){
            try{
                message = bufferedReader.readLine();
                broadcastMessage(message);

            } catch (IOException e) {
                closeSocket(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }


    public void broadcastMessage(String message){
        for(ClientHandler clientHandler: clientHandlers){
            try {
                if(!clientHandler.userName.equals(userName)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeSocket(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeUserFromChat(){
        clientHandlers.remove(this);
        broadcastMessage("Server: " + userName + " has left the chat");
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

    public void closeSocket(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeUserFromChat();
        try {
            if(socket != null){
                socket.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
