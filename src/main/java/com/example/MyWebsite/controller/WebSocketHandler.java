package com.example.MyWebsite.controller;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@EnableWebSocket
public class WebSocketHandler extends TextWebSocketHandler implements WebSocketConfigurer {

    // Saare connected clients ko track karne ke liye
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private static WebSocketSession adminSession = null;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket endpoint ko '/signaling' par map kiya aur CORS allow kiya
        registry.addHandler(this, "/signaling").setAllowedOrigins("*");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // Agar admin join karta hai toh uski session save kar lo
        if (payload.contains("\"type\":\"join-admin\"")) {
            adminSession = session;
            System.out.println("Admin registered!");
            return;
        }

        // Baki saare messages (offer, answer, ice-candidate) ko broadcast/forward karo
        // User ka message Admin ko bhejo, aur Admin ka User ko
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && !webSocketSession.getId().equals(session.getId())) {
                // Agar message admin se aaya hai toh client ko bhejo, nahi toh admin ko
                if (session == adminSession) {
                    webSocketSession.sendMessage(message);
                } else if (adminSession != null && adminSession.isOpen()) {
                    adminSession.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        if (session == adminSession) {
            adminSession = null;
            System.out.println("Admin disconnected");
        }
        System.out.println("Connection closed: " + session.getId());
    }
}