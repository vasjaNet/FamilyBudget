package org.s3m.chatservice.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class ChatResponse {
    private String sender;
    private String content;
    private String room;
    private Instant timestamp;
    private MessageType type;
}
