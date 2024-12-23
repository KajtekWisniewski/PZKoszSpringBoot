package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.MatchService;
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
    private final MatchService matchService;

    public TeamController(TeamService teamService, PlayerService playerService, MatchService matchService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String listTeamsOnHomePage(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "home";
    }

    @GetMapping("/list")
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "team/list";
    }

    @GetMapping("/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("team", new Team());
        return "team/add";
    }

    @PostMapping("/add")
    public String addTeam(@ModelAttribute Team team) {
        teamService.saveTeam(team);
        return "redirect:/team/" + team.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable long id) {
        List<Player> players = playerService.getPlayersByTeam(id);

        for (Player player : players) {
            playerService.removePlayerFromTeam(player.getId());
        }

        teamService.deleteTeamById(id);
        return "redirect:/team/list";
    }

    @GetMapping("/{id}")
    public String viewTeamDetails(@PathVariable long id, Model model) {
        Team team = teamService.getTeamById(id);
        if (team == null) {
            model.addAttribute("error", "Team not found.");
            return "error";
        }

        List<Player> players = playerService.getPlayersByTeam(id);
        List<Player> playersWithoutTeam = playerService.getPlayersWithoutTeam();
        List<Match> matches = matchService.getMatchesByTeamId(id);

        model.addAttribute("team", team);
        model.addAttribute("players", players);
        model.addAttribute("playersWithoutTeam", playersWithoutTeam);
        model.addAttribute("matches", matches);
        return "team/detail";
    }

    @GetMapping("/{teamId}/player/add")
    public String showAddPlayerForm(@PathVariable long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        model.addAttribute("player", new Player());
        return "player/add";
    }

    @PostMapping("/{teamId}/player/add/{playerId}")
    public String addPlayerToTeam(@PathVariable long teamId, @RequestParam long playerId) {
        playerService.assignPlayerToTeam(playerId, teamId);
        return "redirect:/team/" + teamId;
    }

    @GetMapping("/{teamId}/player/remove/{playerId}")
    public String removePlayerFromTeam(@PathVariable long teamId, @PathVariable long playerId) {
        playerService.removePlayerFromTeam(playerId);
        return "redirect:/team/" + teamId;
    }
}
