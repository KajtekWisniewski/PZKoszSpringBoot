package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;
    private final TeamService teamService;

    public PlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    // Display list of players on the homepage
    @GetMapping("/list")
    public String listPlayers(Model model) {
        model.addAttribute("players", playerService.getAllPlayers());
        return "player/list";
    }

    // Display form to add a new player
    @GetMapping("/add")
    public String showAddPlayerForm(Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("teams", teamService.getAllTeams());  // List of teams to choose from
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
        return "redirect:/player/list";
    }



    // Assign a player to a team
    @PostMapping("/assign/{playerId}/{teamId}")
    public String assignPlayerToTeam(@PathVariable Long playerId, @PathVariable Long teamId) {
        playerService.assignPlayerToTeam(playerId, teamId);
        return "redirect:/player/";
    }

    // Remove a player from a team
    @PostMapping("/remove/{playerId}")
    public String removePlayerFromTeam(@PathVariable Long playerId) {
        playerService.removePlayerFromTeam(playerId);
        return "redirect:/player/";
    }

    // Delete player by ID
    @GetMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerService.deletePlayerById(id);
        return "redirect:/player/";
    }
}
