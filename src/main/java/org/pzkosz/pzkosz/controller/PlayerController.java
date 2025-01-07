package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.PlayerStatisticsService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final PlayerStatisticsService playerStatisticsService;

    public PlayerController(PlayerService playerService, TeamService teamService, PlayerStatisticsService playerStatisticsService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.playerStatisticsService = playerStatisticsService;
    }

    @GetMapping("/list")
    public String listPlayers(Model model) {
        model.addAttribute("players", playerService.getAllPlayers());
        return "player/list";
    }

    @GetMapping("/add")
    public String showAddPlayerForm(Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("teams", teamService.getAllTeams());  
        return "player/add";
    }

    @PostMapping("/add")
    public String addPlayer(@ModelAttribute Player player, @RequestParam(required = false) Long teamId) {
        if (teamId != null) {
            Team team = teamService.getTeamById(teamId);
            if (team == null) {
                throw new IllegalArgumentException("Team not found for ID: " + teamId);
            }
            player.setTeam(team);
        }

        playerService.savePlayer(player);
        return "redirect:/player/" + player.getId();
    }



    @PostMapping("/{teamId}/player/add")
    public String addPlayerToTeam(@PathVariable long teamId, @RequestParam long playerId) {
        playerService.assignPlayerToTeam(playerId, teamId);
        return "redirect:/team/" + teamId;
    }

    @PostMapping("/remove/{playerId}")
    public String removePlayerFromTeam(@PathVariable Long playerId) {
        playerService.removePlayerFromTeam(playerId);
        return "redirect:/player/list";
    }

    @GetMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerService.deletePlayerById(id);
        return "redirect:/player/list";
    }

    @GetMapping("/{id}")
    public String getPlayerDetail(@PathVariable("id") Long id, Model model) {
        Player player = playerService.getPlayerById(id);
        if (player != null) {
            model.addAttribute("player", player);
            List<PlayerStatistics> statistics = playerStatisticsService.getStatisticsByPlayerId(id);

            model.addAttribute("statistics", statistics);
            return "player/detail";
        } else {
            model.addAttribute("error", "Player not found");
            return "error";
        }
    }

}
