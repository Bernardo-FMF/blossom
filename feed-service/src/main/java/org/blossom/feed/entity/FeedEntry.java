package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Feed_Entry")
public class FeedEntry {
    @PrimaryKey
    private FeedEntryKey key;

    public int getUserId() {
        return key.getUserId();
    }

    public String getPostId() {
        return key.getPostId();
    }

    @PrimaryKeyClass
    @AllArgsConstructor
    @Getter
    public static class FeedEntryKey implements Serializable {

        @PrimaryKeyColumn(name = "userId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private int userId;

        @PrimaryKeyColumn(name = "postId", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
        private String postId;
    }
}
