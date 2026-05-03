package org.s3m.chatservice.controller;

import org.s3m.chatservice.model.ChatMessage;
import org.s3m.chatservice.model.ChatResponse;
import org.s3m.chatservice.model.MessageType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage/{room}")
    @SendTo("/topic/room/{room}")
    public ChatResponse sendMessage(@DestinationVariable String room, @Payload ChatMessage message) {

        return ChatResponse.builder()
                .sender(message.getSender())
                .content(message.getContent())
                .room(room)
                .timestamp(Instant.now())
                .type(MessageType.CHAT)
                .build();
    }

    @MessageMapping("/chat.join/{room}")
    @SendTo("/topic/room/{room}")
    public ChatResponse joinRoom(@DestinationVariable String room, @Payload ChatMessage message) {

        return ChatResponse.builder()
                .sender(message.getSender())
                .content(message.getSender() + " joined the room")
                .room(room)
                .timestamp(Instant.now())
                .type(MessageType.JOIN)
                .build();
    }

    @RestController
    @RequestMapping("/api/rooms")
    public class RoomController {
        private final Set<String> rooms = ConcurrentHashMap.newKeySet();

        @GetMapping
        public Set<String> getRooms() { return rooms; }

        @PostMapping
        public String createRoom(@RequestBody Map<String, String> body) {
            String room = body.get("name");
            rooms.add(room);
            return room;
        }
    }
}