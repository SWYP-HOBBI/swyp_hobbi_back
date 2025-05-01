package swyp.hobbi.swyphobbiback.post_image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
