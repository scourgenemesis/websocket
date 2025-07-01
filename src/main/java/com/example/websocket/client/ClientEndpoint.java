package com.example.websocket.client;


import jakarta.websocket.*;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@jakarta.websocket.ClientEndpoint
public class ClientEndpoint {
    private Session session;
    private String username;
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public ClientEndpoint(String username) {
        this.username = username;
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("User " + username + " connected!");
        this.session = session;
        session.getAsyncRemote().sendText("USERNAME: "+ username);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(username + " has left the chat!");
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
        System.out.println("Enter your message (or type 'exit' to quit): ");
        while (true) {
            String message = scanner.nextLine();
            if (message.contains("/allsessions")) {
                System.out.println("All sessions: ");
                sessions.forEach(s -> System.out.println(" - " + s.getId()));
            }
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            sendMessage(username + ": " + message);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine().trim();
        ClientEndpoint client = new ClientEndpoint(username);
        try {
            client.connectToServer();
            client.userInputLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}