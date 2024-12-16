package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.PlayerStatisticsRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlayerStatisticsService {

    private final PlayerStatisticsRepository playerStatisticsRepository;

    public PlayerStatisticsService(PlayerStatisticsRepository playerStatisticsRepository) {
        this.playerStatisticsRepository = playerStatisticsRepository;
    }

    public PlayerStatistics savePlayerStatistics(PlayerStatistics playerStatistics) {
        return playerStatisticsRepository.save(playerStatistics);
    }

    public List<PlayerStatistics> getStatisticsByPlayerId(long playerId) {
        return playerStatisticsRepository.findByPlayerId(playerId);
    }

    public List<PlayerStatistics> findByMatchAndTeam(long matchId, Team team) {
        return playerStatisticsRepository.findByMatch_IdAndTeam_Id(matchId, team.getId());
    }

    public PlayerStatistics getStatisticsById(long statId) {
        return playerStatisticsRepository.findById(statId).orElse(null);
    }

    public void updateStatistics(long statId, PlayerStatistics updatedStat) {
        PlayerStatistics existingStat = getStatisticsById(statId);
        if (existingStat != null) {
            existingStat.setPointsScored(updatedStat.getPointsScored());
            existingStat.setRebounds(updatedStat.getRebounds());
            playerStatisticsRepository.save(existingStat);
        }
    }

    public void deleteStatistics(long statId) {
        PlayerStatistics stat = getStatisticsById(statId);
        if (stat != null) {
            playerStatisticsRepository.delete(stat);
        }
    }

}
