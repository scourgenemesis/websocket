package com.example.websocket.server;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/chat")
public class ChatEndpoint {
    private Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());


    @OnOpen
    private void onOpen(Session session) {
        System.out.println("New user connected!");
        sessions.add(session);
    }

    @OnMessage
    private void onMessage(Session session, String message) {
        System.out.println(session + ": " + message);

    }

    @OnClose
    private void onClosed(Session session) {
        sessions.remove(session);
        System.out.println("User disconnected");
    }

    public void broadcast(String message) {
        sessions.forEach(s -> {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(message);
            }
        });
    }
}
