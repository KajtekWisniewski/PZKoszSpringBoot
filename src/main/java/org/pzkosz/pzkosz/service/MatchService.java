package org.pzkosz.pzkosz.service;

import jakarta.transaction.Transactional;
import org.pzkosz.pzkosz.model.Match;
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date matchDate;
        try {
            matchDate = dateFormat.parse(matchDateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date");
        }

        Match match = new Match();
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setMatchDate(matchDate);

        return matchRepository.save(match);
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

    public Integer getTeamScore(Long matchId, Long teamId) {
        Integer score = playerStatisticsRepository.calculateTeamScore(matchId, teamId);
        return score != null ? score : 0;
    }

    @Transactional
    public void updateMatchScores(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Integer team1Score = playerStatisticsRepository.calculateTeamScore(matchId, match.getTeam1().getId());
        Integer team2Score = playerStatisticsRepository.calculateTeamScore(matchId, match.getTeam2().getId());

        match.setTeam1Score(team1Score != null ? team1Score : 0);
        match.setTeam2Score(team2Score != null ? team2Score : 0);

        matchRepository.save(match);
    }

    public List<Match> getMatchesByTeamId(long teamId) {
        return matchRepository.findMatchesByTeamId(teamId);
    }

    public List<Match> searchMatches(String query) {
        try {
            Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse(query);
            return matchRepository.findByMatchDateOrTeam1NameContainingIgnoreCaseOrTeam2NameContainingIgnoreCase(searchDate, query, query);
        } catch (ParseException e) {
            return matchRepository.findByTeam1NameContainingIgnoreCaseOrTeam2NameContainingIgnoreCase(query, query);
        }
    }

    public void deleteMatchById(long id) {
        matchRepository.deleteById(id);
    }

}
