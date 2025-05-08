package swyp.hobbi.swyphobbiback.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.comment.domain.Comment;
import swyp.hobbi.swyphobbiback.comment.dto.CommentCountProjection;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = """
        SELECT c.comment_id
        FROM post_comment c
        JOIN post p on c.post_id = p.post_id
        WHERE c.post_id = :postId
        ORDER BY c.comment_id
        LIMIT :limit
        """, nativeQuery = true)
    List<Long> findCommentIds(@Param("postId") Long postId, @Param("limit") Integer limit);

    @Query(value = """
        SELECT c.comment_id
        FROM post_comment c
        JOIN post p on c.post_id = p.post_id
        WHERE c.post_id = :postId AND c.comment_id > :lastCommentId
        ORDER BY c.comment_id
        LIMIT :limit
        """, nativeQuery = true)
    List<Long> findCommentIds(@Param("postId") Long postId, @Param("lastCommentId") Long lastCommentId,  @Param("limit") Integer limit);

    @Query(value = """
        SELECT DISTINCT c
        FROM Comment c
        LEFT JOIN c.user u
        WHERE c.commentId IN (:commentIds)
        """)
    List<Comment> findCommentWithUser(@Param("commentIds") List<Long> commentIds);

    @Query(value = """
        SELECT COUNT(*) FROM (
                SELECT c.comment_id FROM post_comment c
                JOIN post p on c.post_id = p.post_id
                WHERE c.post_id = :postId AND c.parent_comment_id = :parentCommentId
                LIMIT :limit
        ) AS t
        """, nativeQuery = true)
    Long countBy(@Param("postId") Long postId, @Param("parentCommentId") Long parentCommentId, @Param("limit") Long limit);

    @Query(value = """
        SELECT COUNT(c.comment_id) FROM post_comment c
        JOIN post p on c.post_id = p.post_id
        WHERE c.post_id = :postId AND c.deleted = false
        """, nativeQuery = true)
    Long countByPostId(@Param("postId") Long postId);

    @Query(value = """
        SELECT c.post_id AS postId, COUNT(c.comment_id) AS commentCount FROM post_comment c
        JOIN post p on c.post_id = p.post_id
        WHERE c.post_id IN (:postIds) AND c.deleted = false
        GROUP BY c.post_id
        """, nativeQuery = true)
    List<CommentCountProjection> countsByPostIds(@Param("postIds") List<Long> postIds);
}
