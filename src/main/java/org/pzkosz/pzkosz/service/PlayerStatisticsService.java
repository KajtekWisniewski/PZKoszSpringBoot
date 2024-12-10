package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.repository.PlayerStatisticsRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatisticsService {

    private final PlayerStatisticsRepository playerStatisticsRepository;

    public PlayerStatisticsService(PlayerStatisticsRepository playerStatisticsRepository) {
        this.playerStatisticsRepository = playerStatisticsRepository;
    }

    public PlayerStatistics savePlayerStatistics(PlayerStatistics playerStatistics) {
        return playerStatisticsRepository.save(playerStatistics);
    }
}
