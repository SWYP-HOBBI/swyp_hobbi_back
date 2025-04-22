package swyp.hobbi.swyphobbiback.hobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;

@Repository
public interface HobbyTagRepository extends JpaRepository<HobbyTag,Long> {
}
