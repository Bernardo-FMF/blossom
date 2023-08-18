package org.blossom.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "Blossom_Post")
public class Post {
    @Id
    private String id;

    @Field(name = "description")
    private String description;

    @Field(name = "media")
    private String[] media;

    @Field(name = "hashtags")
    private String[] hashtags;

    @Field(name = "user_id")
    private int userId;

    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt;
}
