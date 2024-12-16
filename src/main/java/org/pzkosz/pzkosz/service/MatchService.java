package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.TeamRepository;
import org.pzkosz.pzkosz.repository.MatchRepository;
import org.pzkosz.pzkosz.repository.PlayerStatisticsRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerService playerService;
    private final TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository, PlayerStatisticsRepository playerStatisticsRepository,
                        PlayerService playerService, TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.playerStatisticsRepository = playerStatisticsRepository;
        this.playerService = playerService;
        this.teamRepository = teamRepository;
    }

    public Match createMatch(Long team1Id, Long team2Id, String matchDateStr) {
        Team team1 = teamRepository.findById(team1Id).orElseThrow(() -> new RuntimeException("Team not found"));
        Team team2 = teamRepository.findById(team2Id).orElseThrow(() -> new RuntimeException("Team not found"));

        // Parse the match date from string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date matchDate;
        try {
            matchDate = dateFormat.parse(matchDateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date");
        }

        // Create and save the match
        Match match = new Match();
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setMatchDate(matchDate);

        return matchRepository.save(match);
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

    public List<Match> getMatchesBeforeDate(Date date) {
        return matchRepository.findByMatchDateBefore(date);
    }

    public List<Match> getMatchesAfterDate(Date date) {
        return matchRepository.findByMatchDateAfter(date);
    }

    public Match getMatchById(long id) {
        return matchRepository.findById(id).orElse(null);
    }

//    public List<Match> searchMatches(String query) {
//        return matchRepository.findByDateOrTeamsRegex(query);
//    }

}
