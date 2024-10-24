package com.bptn.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private static final int PORT = 1234;



    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    //method that starts the server

    public void startServer(){
        try {
            // keep listening for new connection
            System.out.println("Waiting for new connections....");
            while(!serverSocket.isClosed()){
                System.out.println("Chat Server running on port " + PORT);
                Socket socket = serverSocket.accept();
                System.out.println("New user has connected.");
                //instantiate new ClientHandler that will handle each client connection
                ClientHandler clientHandler = new ClientHandler(socket);

                //create a new thread for each client connection
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            closeServer();
        }
    }

    public void closeServer(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
            
        } catch (IOException e) {
            System.out.println("Error shutting down server: " + e.getMessage());

        }

    }




    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        //start the server
        server.startServer();
    }
    
}
