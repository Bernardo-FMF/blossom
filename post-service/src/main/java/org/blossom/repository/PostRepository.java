package org.blossom.repository;

import org.blossom.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByUserId(int userId, Pageable pageable);

    Page<Post> findByHashtagsIn(String query, Pageable page);

    void deleteByUserId(int userId);
}
