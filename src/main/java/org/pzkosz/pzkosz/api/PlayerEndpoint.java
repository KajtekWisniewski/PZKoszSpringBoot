package org.pzkosz.pzkosz.api;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.PlayerStatisticsService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerEndpoint {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final PlayerStatisticsService playerStatisticsService;

    public PlayerEndpoint(PlayerService playerService, TeamService teamService, PlayerStatisticsService playerStatisticsService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.playerStatisticsService = playerStatisticsService;
    }

    
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlayerById(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    
    @PostMapping
    public ResponseEntity<?> createPlayer(@RequestBody Player player, @RequestParam(required = false) Long teamId) {
        if (teamId != null) {
            Team team = teamService.getTeamById(teamId);
            if (team == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team not found for ID: " + teamId);
            }
            player.setTeam(team);
        }

        Player createdPlayer = playerService.savePlayer(player);
        return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Long id, @RequestBody Player playerUpdates) {
        Player existingPlayer = playerService.getPlayerById(id);
        if (existingPlayer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        existingPlayer.setName(playerUpdates.getName());
        existingPlayer.setPosition(playerUpdates.getPosition());
        existingPlayer.setTeam(playerUpdates.getTeam()); 

        Player updatedPlayer = playerService.savePlayer(existingPlayer);
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        Player existingPlayer = playerService.getPlayerById(id);
        if (existingPlayer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        playerService.deletePlayerById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> getPlayerStatistics(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        List<PlayerStatistics> statistics = playerStatisticsService.getStatisticsByPlayerId(id);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @PostMapping("/{playerId}/assign-to-team/{teamId}")
    public ResponseEntity<?> assignPlayerToTeam(@PathVariable Long playerId, @PathVariable Long teamId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }

        playerService.assignPlayerToTeam(playerId, teamId);
        return ResponseEntity.status(HttpStatus.OK).body("Player assigned to team successfully");
    }

    
    @PostMapping("/{playerId}/remove-from-team")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        playerService.removePlayerFromTeam(playerId);
        return ResponseEntity.status(HttpStatus.OK).body("Player removed from team successfully");
    }
}
