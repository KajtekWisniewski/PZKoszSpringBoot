package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // Get all teams
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    // Get team by ID
    public Team getTeamById(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        return team.orElse(null);
    }

    // Save a team
    public void saveTeam(Team team) {
        teamRepository.save(team);
    }

    // Delete a team by ID
    public void deleteTeamById(Long id) {
        teamRepository.deleteById(id);
    }
}
