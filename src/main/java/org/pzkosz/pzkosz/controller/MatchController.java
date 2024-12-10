package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.PlayerStatistics;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.PlayerStatisticsService;
import org.pzkosz.pzkosz.service.TeamService;
import org.pzkosz.pzkosz.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerStatisticsService playerStatisticsService;

    // Display match creation form
    @GetMapping("/add")
    public String showCreateMatchForm(Model model) {
        List<Team> teams = teamService.getAllTeams();
        List<Player> allPlayers = playerService.getAllPlayers();  // Get all players for the form

        model.addAttribute("teams", teams);
        model.addAttribute("players", allPlayers);  // Add all players for player selection
        return "match/add";  // Thymeleaf template for creating a match
    }

    // Handle match creation form submission
    @PostMapping("/add")
    public String createMatch(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id,
            @RequestParam List<Long> team1PlayerIds,  // List of player IDs for team 1
            @RequestParam List<Long> team2PlayerIds,  // List of player IDs for team 2
            @RequestParam List<Integer> team1Points,  // Points for team 1 players
            @RequestParam List<Integer> team2Points,  // Points for team 2 players
            @RequestParam Date matchDate,
            Model model) {

        // Get teams by IDs
        Team team1 = teamService.getTeamById(team1Id);
        Team team2 = teamService.getTeamById(team2Id);

        // Get players by IDs for each team
        List<Player> team1Players = playerService.getPlayersByIds(team1PlayerIds);
        List<Player> team2Players = playerService.getPlayersByIds(team2PlayerIds);

        // Create PlayerStatistics for each player in both teams
        List<PlayerStatistics> playerStatistics = new ArrayList<>();

        // Create statistics for team 1 players
        for (int i = 0; i < team1PlayerIds.size(); i++) {
            Player player = team1Players.get(i);
            PlayerStatistics stats = new PlayerStatistics();
            stats.setPlayer(player);
            stats.setPointsScored(team1Points.get(i));
            playerStatistics.add(stats);
        }

        // Create statistics for team 2 players
        for (int i = 0; i < team2PlayerIds.size(); i++) {
            Player player = team2Players.get(i);
            PlayerStatistics stats = new PlayerStatistics();
            stats.setPlayer(player);
            stats.setPointsScored(team2Points.get(i));
            playerStatistics.add(stats);
        }

        // Create the match
        Match match = matchService.createMatch(team1, team2, team1Players, team2Players, playerStatistics, matchDate);

        model.addAttribute("match", match);  // Add the match object to the model for confirmation
        return "redirect:/match/" + match.getId();  // Redirect to match details page after creation
    }

}

