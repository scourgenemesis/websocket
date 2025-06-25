package com.example.websocket.client;


import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;

import java.net.URI;
import java.util.Scanner;

@jakarta.websocket.ClientEndpoint
public class ClientEndpoint {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New user connected!");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(session.getId() + ": " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(session.getId() + " has left the chat!");
        this.session = null;
    }


    private void connectToServer() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String serverUri = "ws://localhost:8080/folder/app";
        container.connectToServer(this, URI.create(serverUri));
    }

    private void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public void userInputLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your message: ");
        while (true) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            sendMessage(message);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        ClientEndpoint client = new ClientEndpoint();
            try {
                client.connectToServer();
                client.userInputLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
