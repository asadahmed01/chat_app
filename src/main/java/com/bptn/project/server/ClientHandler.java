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
        } catch (IOException e) {
            closeSocket();
        }
        
    }

    @Override
    public void run() {
        try {
            
            String message;
            while(socket.isConnected()){
                message = bufferedReader.readLine();
                //broadcast the message to all connected clients
                broadcastMessage(message);
            }
        } catch (IOException e) {
            closeSocket();
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
                closeSocket();
            }
        }
    }

    public void removeUserFromChat(){
        clientHandlers.remove(this);
        broadcastMessage("Server: " + userName + " has left the chat");
    }

    public void closeSocket(){
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
