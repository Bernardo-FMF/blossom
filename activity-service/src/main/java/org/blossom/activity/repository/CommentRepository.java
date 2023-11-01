package org.blossom.activity.repository;

import org.blossom.activity.entity.Comment;
import org.blossom.activity.projection.CommentCountProjection;
import org.blossom.activity.projection.CommentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Modifying
    @Query(value = "UPDATE Comment c SET c.parentComment.id = :parentId, c.topLevelComment.id = :topLevelCommentId WHERE c.id = :commentId")
    void updateParentComment(@Param("commentId") Integer commentId, @Param("topLevelCommentId") Integer topLevelCommentId, @Param("parentId") Integer parentId);

    void deleteByPostId(String postId);

    Page<Comment> findByUserId(int userId, Pageable pageable);

    @Query(value = "SELECT NEW org.blossom.projection.CommentProjection(c.id, c.user.id, c.postId, c.commentContent, c.parentComment.id, c.createdAt, c.updatedAt, c.isDeleted, SUM(CASE WHEN r.id IS NOT NULL THEN 1 ELSE 0 END)) " +
            "FROM Comment c LEFT JOIN Comment r ON c.id = r.topLevelComment.id " +
            "WHERE c.postId = :postId AND c.parentComment IS NULL " +
            "GROUP BY c.id",
            countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.postId = :postId AND c.parentComment IS NULL")
    Page<CommentProjection> findTopLevelCommentsWithReplyCount(@Param("postId") String postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.topLevelComment.id = :topLevelCommentId")
    Page<Comment> findByTopLevelCommentId(@Param("topLevelCommentId") Integer topLevelCommentId, Pageable pageable);

    @Query("SELECT NEW org.blossom.projection.CommentCountProjection(" +
            "COALESCE(SUM(1), 0)) " +
            "FROM Comment c " +
            "WHERE c.postId = :postId")
    CommentCountProjection getCommentCountWithNoUser(@Param("postId") String postId);

    @Query("SELECT NEW org.blossom.projection.CommentCountProjection(" +
            "COALESCE(SUM(1), 0)," +
            "COUNT(CASE WHEN c.user.id = :userId THEN 1 END) > 0) " +
            "FROM Comment c " +
            "WHERE c.postId = :postId")
    CommentCountProjection getCommentCount(@Param("postId") String postId, @Param("userId") Integer userId);
}