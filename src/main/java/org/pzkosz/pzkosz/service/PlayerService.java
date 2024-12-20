package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.repository.PlayerRepository;
import org.pzkosz.pzkosz.model.Team;
import org.springframework.stereotype.Service;
import org.pzkosz.pzkosz.service.TeamService;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;

    public PlayerService(PlayerRepository playerRepository, TeamService teamService) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public List<Player> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    public void deletePlayerById(Long id) {
        playerRepository.deleteById(id);
    }

    public void assignPlayerToTeam(Long playerId, Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Team team = teamService.getTeamById(teamId);  // Fetch the team by ID using the teamService
        player.setTeam(team);  // Assign the team to the player
        playerRepository.save(player);
    }

    public void removePlayerFromTeam(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found"));
        player.setTeam(null);
        playerRepository.save(player);
    }

    public List<Player> getPlayersWithoutTeam() {
        return playerRepository.findByTeamIsNull();
    }

    public List<Player> getPlayersByIds(List<Long> playerIds) {
        return playerRepository.findAllById(playerIds);
    }

    public List<Player> searchPlayers(String query) {
        return playerRepository.findByNameRegex(query);
    }
}
