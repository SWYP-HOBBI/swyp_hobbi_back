package swyp.hobbi.swyphobbiback.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.post.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
