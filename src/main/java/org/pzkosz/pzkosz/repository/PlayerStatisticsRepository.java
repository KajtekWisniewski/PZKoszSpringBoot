package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Long> {
}
