package org.s3m.relationservice.service;

import lombok.RequiredArgsConstructor;
import org.s3m.relationservice.entity.UserNode;
import org.s3m.relationservice.repository.UserNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final UserNodeRepository userNodeRepository;

    @Transactional
    public UserNode getOrCreateUser(UUID userId, String name) {
        return userNodeRepository.findById(userId)
                .orElseGet(() -> {
                    UserNode newUser = UserNode.builder()
                            .userId(userId)
                            .name(name)
                            .build();
                    return userNodeRepository.save(newUser);
                });
    }

    @Transactional
    public void addFriend(UUID userId, UUID friendId) {
        UserNode user = findUserOrThrow(userId);
        UserNode friend = findUserOrThrow(friendId);
        user.getFriends().add(friend);
        userNodeRepository.save(user);
    }

    @Transactional
    public void addChild(UUID parentId, UUID childId) {
        UserNode parent = findUserOrThrow(parentId);
        UserNode child = findUserOrThrow(childId);
        parent.getChildren().add(child);
        child.getParents().add(parent);
        userNodeRepository.save(parent);
        userNodeRepository.save(child);
    }

    @Transactional(readOnly = true)
    public Set<UserNode> getFriends(UUID userId) {
        return findUserOrThrow(userId).getFriends();
    }

    @Transactional(readOnly = true)
    public Set<UserNode> getChildren(UUID userId) {
        return findUserOrThrow(userId).getChildren();
    }

    @Transactional(readOnly = true)
    public Set<UserNode> getParents(UUID userId) {
        return findUserOrThrow(userId).getParents();
    }

    private UserNode findUserOrThrow(UUID userId) {
        return userNodeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }
}
