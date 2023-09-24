package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Local_Post")
public class LocalPost {
    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;
    private int userId;
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private String[] media;
    private String description;
    @PrimaryKeyColumn(name = "created_at", ordinal = 1, ordering = Ordering.DESCENDING)
    private Date createdAt;
}
