package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Long> {
    List<PlayerStatistics> findByPlayerId(long playerId);

    List<PlayerStatistics> findByMatch_IdAndTeam_Id(Long matchId, Long teamId);
}
