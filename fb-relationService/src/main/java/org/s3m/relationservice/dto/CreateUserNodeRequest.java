package org.s3m.relationservice.dto;

import java.util.UUID;

public record CreateUserNodeRequest(UUID userId, String name) {
}
