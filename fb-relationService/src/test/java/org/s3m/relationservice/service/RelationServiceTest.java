package org.s3m.relationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.relationservice.entity.UserNode;
import org.s3m.relationservice.repository.UserNodeRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

    @Mock
    private UserNodeRepository userNodeRepository;

    @InjectMocks
    private RelationService relationService;

    @Test
    void testGetOrCreateUser_NewUser() {
        UUID userId = UUID.randomUUID();
        String name = "Test User";
        when(userNodeRepository.findById(userId)).thenReturn(Optional.empty());
        when(userNodeRepository.save(any(UserNode.class))).thenAnswer(i -> i.getArguments()[0]);

        UserNode result = relationService.getOrCreateUser(userId, name);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(name, result.getName());
        verify(userNodeRepository).save(any(UserNode.class));
    }

    @Test
    void testAddFriend() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        UserNode user = UserNode.builder().userId(userId).name("User").build();
        UserNode friend = UserNode.builder().userId(friendId).name("Friend").build();

        when(userNodeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userNodeRepository.findById(friendId)).thenReturn(Optional.of(friend));

        relationService.addFriend(userId, friendId);

        assertTrue(user.getFriends().contains(friend));
        verify(userNodeRepository).save(user);
    }

    @Test
    void testAddChild() {
        UUID parentId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();
        UserNode parent = UserNode.builder().userId(parentId).name("Parent").build();
        UserNode child = UserNode.builder().userId(childId).name("Child").build();

        when(userNodeRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(userNodeRepository.findById(childId)).thenReturn(Optional.of(child));

        relationService.addChild(parentId, childId);

        assertTrue(parent.getChildren().contains(child));
        assertTrue(child.getParents().contains(parent));
        verify(userNodeRepository).save(parent);
        verify(userNodeRepository).save(child);
    }
}
