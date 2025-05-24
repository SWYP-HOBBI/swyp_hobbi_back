package swyp.hobbi.swyphobbiback.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.challenge.domain.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
