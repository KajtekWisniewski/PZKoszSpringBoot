package org.pzkosz.pzkosz.dto;

import lombok.Getter;
import lombok.Setter;

public class PlayerStatisticsDTO {
    private Long playerId;
    private int pointsScored;
    private int rebounds;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(int pointsScored) {
        this.pointsScored = pointsScored;
    }

    public int getRebounds() {
        return rebounds;
    }

    public void setRebounds(int rebounds) {
        this.rebounds = rebounds;
    }
}