package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Long> {
    List<PlayerStatistics> findByPlayerId(long playerId);

    List<PlayerStatistics> findByMatch_IdAndTeam_Id(Long matchId, Long teamId);

    @Query("SELECT SUM(ps.pointsScored) " +
            "FROM PlayerStatistics ps " +
            "WHERE ps.match.id = :matchId AND ps.player.team.id = :teamId")
    Integer calculateTeamScore(@Param("matchId") Long matchId, @Param("teamId") Long teamId);

}
