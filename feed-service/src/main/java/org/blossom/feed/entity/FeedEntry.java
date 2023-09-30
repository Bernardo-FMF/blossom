package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Feed_Entry")
public class FeedEntry {
    @PrimaryKey
    private UUID id;
    private int userId;
    private String postId;
}
