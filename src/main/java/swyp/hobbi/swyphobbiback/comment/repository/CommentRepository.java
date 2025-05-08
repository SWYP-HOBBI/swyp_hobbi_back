package swyp.hobbi.swyphobbiback.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.comment.domain.Comment;

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
}
