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
@Table("Blossom_Local_Post_By_User")
public class LocalPostByUser {
    @PrimaryKey
    private LocalPostByUserKey key;
    @Indexed
    private String postId;
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> media;
    private String description;

    @PrimaryKeyClass
    @AllArgsConstructor
    @Getter
    public static class LocalPostByUserKey implements Serializable {
        @PrimaryKeyColumn(name = "userId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private int userId;

        @PrimaryKeyColumn(name = "createdAt", ordinal = 1, ordering = Ordering.DESCENDING)
        private Instant createdAt;
    }
}
