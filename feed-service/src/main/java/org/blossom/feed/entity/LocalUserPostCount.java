package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Local_User_Post_Count")
public class LocalUserPostCount {
    @PrimaryKey
    private int userId;

    @CassandraType(type = CassandraType.Name.COUNTER)
    private long postCount;

    public void incrementCount() {
        this.postCount++;
    }

    public void decrementCount() {
        this.postCount--;
    }
}