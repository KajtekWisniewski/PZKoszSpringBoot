package org.pzkosz.pzkosz.api;

import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.service.TeamService;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamEndpoint {

    private final TeamService teamService;
    private final PlayerService playerService;
    private final MatchService matchService;

    public TeamEndpoint(TeamService teamService, PlayerService playerService, MatchService matchService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable long id) {
        Team team = teamService.getTeamById(id);
        if (team == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team createdTeam = teamService.saveTeam(team);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable long id, @RequestBody Team updatedTeam) {
        Team existingTeam = teamService.getTeamById(id);
        if (existingTeam == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingTeam.setName(updatedTeam.getName());
        existingTeam.setDescription(updatedTeam.getDescription());
        existingTeam.setCity(updatedTeam.getCity());
        existingTeam.setWins(updatedTeam.getWins());
        existingTeam.setLosses(updatedTeam.getLosses());

        Team savedTeam = teamService.saveTeam(existingTeam);
        return new ResponseEntity<>(savedTeam, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable long id) {
        Team existingTeam = teamService.getTeamById(id);
        if (existingTeam == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Player> players = playerService.getPlayersByTeam(id);
        for (Player player : players) {
            playerService.removePlayerFromTeam(player.getId());
        }

        teamService.deleteTeamById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{teamId}/players")
    public ResponseEntity<List<Player>> getPlayersByTeam(@PathVariable long teamId) {
        List<Player> players = playerService.getPlayersByTeam(teamId);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> addPlayerToTeam(@PathVariable long teamId, @PathVariable long playerId) {
        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        playerService.assignPlayerToTeam(playerId, teamId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> removePlayerFromTeam(@PathVariable long teamId, @PathVariable long playerId) {
        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        playerService.removePlayerFromTeam(playerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{teamId}/matches")
    public ResponseEntity<List<Match>> getMatchesByTeam(@PathVariable long teamId) {
        List<Match> matches = matchService.getMatchesByTeamId(teamId);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }
}
