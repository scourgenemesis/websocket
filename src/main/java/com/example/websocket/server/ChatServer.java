package com.example.websocket.server;

import jakarta.websocket.DeploymentException;
import org.glassfish.tyrus.server.Server;

public class ChatServer {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/folder", null, ChatEndpoint.class);
        try {
            server.start();
            System.out.println("Server is running!");
            while(true) {}
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
        finally {
            server.stop();
        }
    }
}
