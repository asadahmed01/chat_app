package com.bptn.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    //method that starts the server

    public void startServer(){
        try {
            // keep listening for new connection
            System.out.println("Waiting for new connections....");
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New user has connected.");
                //instantiate new ClientHandler that will handle each client connection
                ClientHandler clientHandler = new ClientHandler(socket);

                //create a new thread for each client connection
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
            
        } catch (IOException e) {
            closeServer();
        }
    }

    public void closeServer(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
            
        } catch (IOException e) {
            System.out.println("Something went wrong in the server.");
            e.printStackTrace();
        }

    }




    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        //start the server
        server.startServer();
    }
    
}
