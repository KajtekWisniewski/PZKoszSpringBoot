package org.pzkosz.pzkosz.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.MatchRepository;
import org.pzkosz.pzkosz.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchStatsService {
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public MatchStatsService(MatchRepository matchRepository, TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void updateTeamWinsAndLosses(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        List<Match> matches = matchRepository.findMatchesByTeamId(teamId);
        int wins = 0;
        int losses = 0;

        for (Match match : matches) {
            if (match.getTeam1().getId() == teamId) {
                if (match.getTeam1Score() > match.getTeam2Score()) wins++;
                else if (match.getTeam1Score() < match.getTeam2Score()) losses++;
            } else {
                if (match.getTeam2Score() > match.getTeam1Score()) wins++;
                else if (match.getTeam2Score() < match.getTeam1Score()) losses++;
            }
        }

        team.setWins(wins);
        team.setLosses(losses);
        teamRepository.save(team);
    }

    @Transactional
    public void updateAllTeamsWinsAndLosses() {
        List<Team> teams = teamRepository.findAll();
        teams.forEach(team -> updateTeamWinsAndLosses(team.getId()));
    }
}
