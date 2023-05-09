package study.springsecurejwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.springsecurejwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
