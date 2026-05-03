package org.s3m.chatservice.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomId;
    private String sender;
    private String content;
}