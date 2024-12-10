package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.MatchRepository;
import org.pzkosz.pzkosz.repository.PlayerStatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerService playerService;

    public MatchService(MatchRepository matchRepository, PlayerStatisticsRepository playerStatisticsRepository, PlayerService playerService) {
        this.matchRepository = matchRepository;
        this.playerStatisticsRepository = playerStatisticsRepository;
        this.playerService = playerService;
    }

    public Match createMatch(Team team1, Team team2, List<Player> team1Players, List<Player> team2Players, List<PlayerStatistics> playerStatistics, Date matchDate) {
        // Get match ID after creating the match
        Match match = new Match();
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setMatchDate(matchDate);
        match = matchRepository.save(match);

        // Save Player Statistics and associate with match
        for (PlayerStatistics stats : playerStatistics) {
            stats.setMatch(match);  // Associate the statistics with the match
            playerStatisticsRepository.save(stats);
        }

        // Calculate scores for both teams based on the match ID
        int team1Score = calculateTeamScore(team1Players, match.getId());
        int team2Score = calculateTeamScore(team2Players, match.getId());

        // Update match with the calculated scores
        match.setTeam1Score(team1Score);
        match.setTeam2Score(team2Score);

        // Save updated match with scores
        matchRepository.save(match);

        return match;
    }


    private int calculateTeamScore(List<Player> players, Long matchId) {
        return players.stream()
                .mapToInt(player -> player.getPlayerStatistics().stream()
                        // Filter statistics by the match ID
                        .filter(stats -> stats.getMatch().getId().equals(matchId))
                        .mapToInt(PlayerStatistics::getPointsScored)
                        .sum())
                .sum();
    }

}
