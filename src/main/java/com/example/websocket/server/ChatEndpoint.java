package com.example.websocket.server;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/app")
public class ChatEndpoint {
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New user connected!");
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        broadcast("User " + session.getId() + ": " + message, session);
    }

    @OnClose
    public void onClosed(Session session) {
        sessions.remove(session);
        System.out.println("User disconnected");
    }

    public void broadcast(String message, Session sender) {
        System.out.println("All sessions: ");
        sessions.forEach(s -> System.out.println(" - " + s.getId()));
        synchronized (sessions) {
        sessions.forEach(s -> {
            if (s.isOpen() && !s.getId().equals(sender.getId())) {
                s.getAsyncRemote().sendText(message);
            }
        });
            System.out.println(sender.getId() + ": " + message);
        }
    }
}
