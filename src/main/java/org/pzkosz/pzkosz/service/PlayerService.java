package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.repository.PlayerRepository;
import org.pzkosz.pzkosz.model.Team;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public void deletePlayerById(Long id) {
        playerRepository.deleteById(id);
    }

    public void assignPlayerToTeam(Long playerId, Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Team team = teamService.getTeamById(teamId);  
        player.setTeam(team);  
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

    public List<Player> searchPlayers(String query) {
        return playerRepository.findByNameRegex(query);
    }
}
