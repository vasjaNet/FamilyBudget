package org.s3m.relationservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Node("User")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {
    @Id
    private UUID userId;
    private String name;

    @Builder.Default
    @Relationship(type = "FRIEND", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> friends = new HashSet<>();

    @Builder.Default
    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> children = new HashSet<>();

    @Builder.Default
    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> parents = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNode userNode = (UserNode) o;
        return Objects.equals(userId, userNode.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
