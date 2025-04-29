package swyp.hobbi.swyphobbiback.hobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;

import java.util.List;

@Repository
public interface HobbyTagRepository extends JpaRepository<HobbyTag,Long> {
    List<HobbyTag> findAllByHobbyTagNameIn(List<String> hobbyTagNames);
}
