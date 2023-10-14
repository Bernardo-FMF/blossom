package org.blossom.message.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Blossom_Chat_User")
public class User {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "image_url")
    private String imageUrl;
}
