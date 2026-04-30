package org.s3m.relationservice.controller;

import lombok.RequiredArgsConstructor;
import org.s3m.commonlib.config.ResponseWrapper;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.relationservice.dto.CreateUserNodeRequest;
import org.s3m.relationservice.dto.UserNodeResponse;
import org.s3m.relationservice.entity.UserNode;
import org.s3m.relationservice.service.RelationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

    @PostMapping("/users")
    public ResponseEntity<ResponseWrapper<UserNodeResponse>> createUser(@RequestBody CreateUserNodeRequest request) {
        UserNode user = relationService.getOrCreateUser(request.userId(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("User relation node created successfully", toResponse(user)));
    }

    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<ResponseWrapper<Void>> addFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
        relationService.addFriend(userId, friendId);
        return ResponseEntity.ok(ResponseWrapper.success("Friend added successfully", null));
    }

    @PostMapping("/{parentId}/children/{childId}")
    public ResponseEntity<ResponseWrapper<Void>> addChild(@PathVariable UUID parentId, @PathVariable UUID childId) {
        relationService.addChild(parentId, childId);
        return ResponseEntity.ok(ResponseWrapper.success("Parent-child relation added successfully", null));
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<ResponseWrapper<Set<UserNodeResponse>>> getFriends(@PathVariable UUID userId) {
        Set<UserNode> friends = relationService.getFriends(userId);
        return ResponseEntity.ok(ResponseWrapper.success("Friends retrieved successfully",
                friends.stream().map(this::toResponse).collect(Collectors.toSet())));
    }

    @GetMapping("/{userId}/children")
    public ResponseEntity<ResponseWrapper<Set<UserNodeResponse>>> getChildren(@PathVariable UUID userId) {
        Set<UserNode> children = relationService.getChildren(userId);
        return ResponseEntity.ok(ResponseWrapper.success("Children retrieved successfully",
                children.stream().map(this::toResponse).collect(Collectors.toSet())));
    }

    @GetMapping("/{userId}/parents")
    public ResponseEntity<ResponseWrapper<Set<UserNodeResponse>>> getParents(@PathVariable UUID userId) {
        Set<UserNode> parents = relationService.getParents(userId);
        return ResponseEntity.ok(ResponseWrapper.success("Parents retrieved successfully",
                parents.stream().map(this::toResponse).collect(Collectors.toSet())));
    }

    private UserNodeResponse toResponse(UserNode node) {
        return new UserNodeResponse(node.getUserId(), node.getName());
    }
}
