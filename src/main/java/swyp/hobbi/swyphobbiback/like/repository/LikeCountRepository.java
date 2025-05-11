package swyp.hobbi.swyphobbiback.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.like.domain.LikeCount;
import swyp.hobbi.swyphobbiback.like.dto.LikeCountProjection;

import java.util.List;

@Repository
public interface LikeCountRepository extends JpaRepository<LikeCount, Long> {
    @Query(value = """
        SELECT l.post_id AS postId, l.like_count AS likeCount FROM post_like_count l
        JOIN post p on l.post_id = p.post_id
        WHERE l.post_id IN (:postIds)
        GROUP BY l.post_id
        """, nativeQuery = true)
    List<LikeCountProjection> findLikeCountByPostIds(@Param("postIds") List<Long> postIds);
}
