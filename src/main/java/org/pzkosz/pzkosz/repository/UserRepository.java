package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<PZKoszUser, Long> {
    Optional<PZKoszUser> findByUsername(String username);
}
