package org.pzkosz.pzkosz.dto;

import java.util.List;

public class MatchStatisticsDTO {
    private List<PlayerStatisticsDTO> playerStatistics;

    public List<PlayerStatisticsDTO> getPlayerStatistics() {
        return playerStatistics;
    }

    public void setPlayerStatistics(List<PlayerStatisticsDTO> playerStatistics) {
        this.playerStatistics = playerStatistics;
    }
}
