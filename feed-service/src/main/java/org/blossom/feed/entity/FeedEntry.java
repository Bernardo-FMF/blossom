package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Feed_Entry")
public class FeedEntry {
    @PrimaryKeyColumn(name = "entry_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private int id;

    private int userId;

    private String postId;
}
