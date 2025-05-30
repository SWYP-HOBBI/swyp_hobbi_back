package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp.hobbi.swyphobbiback.user.domain.DeletedUser;

public interface DeletedUserRepository extends JpaRepository<DeletedUser, Long> {
}
