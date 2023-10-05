package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Inverted_Feed_Entry")
public class InvertedFeedEntry {
    @PrimaryKey
    private String postId;
    private int userId;
}
