package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.repository.PlayerRepository;
import org.pzkosz.pzkosz.model.Team;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Get all players
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Get players by team
    public List<Player> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    // Save a player (either with or without a team)
    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    // Delete a player by ID
    public void deletePlayerById(Long id) {
        playerRepository.deleteById(id);
    }

    // Assign a player to a team
    public void assignPlayerToTeam(Long playerId, Long teamId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found"));
        Team team = new Team();  // Find the team by id (you can adjust to your logic)
        team.setId(teamId);  // Set the team ID (this can be fetched from the service if necessary)
        player.setTeam(team);  // Assign the team to the player
        playerRepository.save(player);
    }

    // Remove a player from a team
    public void removePlayerFromTeam(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found"));
        player.setTeam(null);  // Remove the team reference from the player
        playerRepository.save(player);
    }
}
