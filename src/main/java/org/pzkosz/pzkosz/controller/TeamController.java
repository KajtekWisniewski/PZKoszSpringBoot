package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;

    public TeamController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    // Display teams on homepage
    @GetMapping("/")
    public String listTeamsOnHomePage(Model model) {
        System.out.println("Loading homepage with teams...");
        model.addAttribute("teams", teamService.getAllTeams());
        return "home";
    }

    // List all teams explicitly
    @GetMapping("/list")
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "team/list";
    }

    // Show form to add a new team
    @GetMapping("/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("team", new Team());
        return "team/add";
    }

    // Handle form submission for adding a new team
    @PostMapping("/add")
    public String addTeam(@ModelAttribute Team team) {
        teamService.saveTeam(team);
        return "redirect:/team/list";
    }

    // Delete a team by ID
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable long id) {
        teamService.deleteTeamById(id);
        return "redirect:/team/list";
    }

    // Display details of a team by ID, including its players
    @GetMapping("/{id}")
    public String viewTeamDetails(@PathVariable long id, Model model) {
        Team team = teamService.getTeamById(id);
        List<Player> players = playerService.getPlayersByTeam(id);
        model.addAttribute("team", team);
        model.addAttribute("players", players);
        return "team/detail"; // Create "team/detail.html" to display team details and player list
    }

    // Show form to add a new player to a team
    @GetMapping("/{teamId}/player/add")
    public String showAddPlayerForm(@PathVariable long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        model.addAttribute("player", new Player());
        return "player/add"; // Create "player/add.html" for adding a player
    }

    // Handle form submission for adding a player to a team
    @PostMapping("/{teamId}/player/add")
    public String addPlayerToTeam(@PathVariable long teamId, @ModelAttribute Player player) {
        Team team = teamService.getTeamById(teamId);
        player.setTeam(team);
        playerService.savePlayer(player);
        return "redirect:/team/" + teamId; // Redirect to team details page
    }

    // Remove a player from a team
    @GetMapping("/{teamId}/player/remove/{playerId}")
    public String removePlayerFromTeam(@PathVariable long teamId, @PathVariable long playerId) {
        playerService.removePlayerFromTeam(playerId);
        return "redirect:/team/" + teamId; // Redirect to team details page
    }
}
