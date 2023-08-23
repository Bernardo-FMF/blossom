package org.blossom.repository;

import org.blossom.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Modifying
    @Query(value = "UPDATE Comment c SET c.parentComment.id = :parentId WHERE c.id = :commentId")
    void updateParentComment(@Param("commentId") Integer commentId, @Param("parentId") Integer parentId);
}
