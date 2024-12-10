package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
