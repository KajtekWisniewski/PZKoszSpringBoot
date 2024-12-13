package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamId(Long teamId);
    List<Player> findByTeamIsNull();
    @Query("SELECT p FROM Player p WHERE p.name LIKE %:pattern%")
    List<Player> findByNameRegex(@Param("pattern") String pattern);
}