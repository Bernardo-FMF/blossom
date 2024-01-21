package org.blossom.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table("Blossom_Local_User")
public class LocalUser {
    @PrimaryKey
    @Column(value = "id")
    private int id;

    @Column(value = "username")
    private String username;

    @Column(value = "fullName")
    private String fullName;

    @Column(value = "imageUrl")
    private String imageUrl;
}
