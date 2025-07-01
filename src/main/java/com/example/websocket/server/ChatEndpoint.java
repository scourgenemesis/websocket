package com.example.websocket.server;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@ServerEndpoint("/app")
public class ChatEndpoint {
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static Logger logger = Logger.getLogger(ChatEndpoint.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New user connected!");
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.startsWith("USERNAME: ")) {
            session.getUserProperties().put("username", message.substring(9));
        }
        if (message.startsWith("/allsessions")) {
            String sessionList = getActiveSessionsList();
            sendPrivate(session, "Active sessions: \n" + sessionList);
        }
        else {
            broadcast("User " + session.getId() + ": " + message, session);
        }
    }

    @OnClose
    public void onClosed(Session session) {
        sessions.remove(session);
        logger.info("Session opened: " + session.getId());
        System.out.println("User disconnected");
    }

    public void broadcast(String message, Session sender) {
        if (message.contains("/allsessions")) {
            System.out.println("All sessions: ");
            getActiveSessionsList();
        }
        synchronized (sessions) {
            String username = (String) sender.getUserProperties().get("username");
            String formatedMessage = username + ": " + message;
            sessions.forEach(s -> {
                if (s.isOpen() && !s.getId().equals(sender.getId())) {
                    s.getAsyncRemote().sendText(formatedMessage);
                }
            });
            System.out.println(username + ": " + formatedMessage);
        }
    }

    public String getActiveSessionsList() {
        StringBuilder sb = new StringBuilder();

        synchronized (sessions) {
            sessions.forEach(session -> {
                sb.append("Session ID: ").append(session.getId()).append(" Active: ").append(session.isOpen() ? "Yes" : "No").append("\n");
            });
        }
        return sb.toString();
    }

    public void sendPrivate(Session target, String message) {
        try {
            if (target.isOpen()) {
                target.getBasicRemote().sendText("[PRIVATE] " + message);
            }
        } catch (IOException e) {
            logger.severe("Error sending private message: " + e.getMessage());
        }
    }
}