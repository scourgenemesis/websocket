package com.example.websocket.client;

import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

public class MessageHandler {
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private final Set<Session> sessions = new CopyOnWriteArraySet<>();

    public void addSession(Session session) {
        sessions.add(session);
        logger.info("Session added. " + session.getId());

    }

    public void removeSession(Session session) {
        sessions.remove(session);
        logger.info("Session removed " + session.getId());
    }
}
