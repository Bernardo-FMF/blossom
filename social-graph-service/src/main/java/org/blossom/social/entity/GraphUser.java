package org.blossom.social.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Getter
@Setter
@Builder
@Node
public class GraphUser {
    @Id
    private Integer userId;

    String fullName;

    String username;

    String imageUrl;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<GraphUser> following;
}