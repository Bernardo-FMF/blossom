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
@Table("Blossom_Local_User")
public class LocalUser {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private int id;
    private String username;
    private String fullName;
    private String imageUrl;
}
