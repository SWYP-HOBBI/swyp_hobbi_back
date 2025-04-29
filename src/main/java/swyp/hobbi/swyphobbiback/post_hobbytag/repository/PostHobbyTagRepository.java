package swyp.hobbi.swyphobbiback.post_hobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.post_hobbytag.domain.PostHobbyTag;

import java.util.List;

@Repository
public interface PostHobbyTagRepository extends JpaRepository<PostHobbyTag, Long> {
    List<PostHobbyTag> findAllByPostId(Long postId);
}
