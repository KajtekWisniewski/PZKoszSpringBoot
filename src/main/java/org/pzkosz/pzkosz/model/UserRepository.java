package org.pzkosz.pzkosz.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<PZKoszUser, Long> {
    Optional<PZKoszUser> findByUsername(String username);
}
