package swyp.hobbi.swyphobbiback.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;

public interface PostConstructRepository extends JpaRepository<HobbyTag, Long> {
    @Query(value = """
        INSERT INTO hobby_tag
        """)
    void insertHobbyTag();
}
