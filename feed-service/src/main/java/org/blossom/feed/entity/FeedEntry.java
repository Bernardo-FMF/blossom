package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Feed_Entry")
public class FeedEntry {
    @PrimaryKey
    private FeedEntryKey key;

    @Indexed
    @Column(value = "postId")
    private String postId;

    private int postCreatorId;

    @Column(value = "media")
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> media;

    @Column(value = "hashtags")
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> hashtags;

    @Column(value = "description")
    private String description;

    @PrimaryKeyClass
    @AllArgsConstructor
    @Getter
    public static class FeedEntryKey implements Serializable {
        @PrimaryKeyColumn(name = "userId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private int userId;

        @PrimaryKeyColumn(name = "createdAt", ordinal = 1, ordering = Ordering.DESCENDING)
        private Instant createdAt;
    }
}
