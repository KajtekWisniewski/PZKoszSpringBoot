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
import java.util.stream.Collectors;

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

    @GetMapping("/add")
    public String showAddMatchForm(Model model) {
        List<Team> teams = teamService.getAllTeams();
        model.addAttribute("teams", teams);
        return "match/add";
    }

    @PostMapping("/add")
    public String createMatch(@RequestParam("team1") Long team1Id,
                              @RequestParam("team2") Long team2Id,
                              @RequestParam("matchDate") String matchDate, Model model) {

        if (team1Id.equals(team2Id)) {
            model.addAttribute("error", "The teams cannot be the same.");
            return "match/add";  // Return to the form with an error message
        }

        // Call the service to create the match
        Match match = matchService.createMatch(team1Id, team2Id, matchDate);

        // Redirect to the match detail page
        return "redirect:/match/" + match.getId();
    }

    @GetMapping("/archive")
    public String viewMatchArchive(Model model) {
        Date now = new Date();
        List<Match> pastMatches = matchService.getMatchesBeforeDate(now);
        model.addAttribute("matches", pastMatches);
        model.addAttribute("title", "Match Archive");
        return "match/match-archive";
    }

    @GetMapping("/schedule")
    public String viewMatchSchedule(Model model) {
        Date now = new Date();
        List<Match> upcomingMatches = matchService.getMatchesAfterDate(now);
        model.addAttribute("matches", upcomingMatches);
        model.addAttribute("title", "Match Schedule");
        return "match/match-schedule";
    }

    @GetMapping("/{id}")
    public String viewMatchDetails(@PathVariable long id, Model model) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            model.addAttribute("error", "Match not found.");
            return "error";
        }

        // Get players by team
        List<Player> team1Players = playerService.getPlayersByTeam(match.getTeam1().getId());
        List<Player> team2Players = playerService.getPlayersByTeam(match.getTeam2().getId());

        // Get player statistics for the match
        List<PlayerStatistics> team1Stats = playerStatisticsService.findByMatchAndTeam(id, match.getTeam1());
        List<PlayerStatistics> team2Stats = playerStatisticsService.findByMatchAndTeam(id, match.getTeam2());

        List<Player> team1WithoutStats = team1Players.stream()
                .filter(player -> team1Stats.stream().noneMatch(stat -> stat.getPlayer().getId().equals(player.getId())))
                .toList();

        List<Player> team2WithoutStats = team2Players.stream()
                .filter(player -> team2Stats.stream().noneMatch(stat -> stat.getPlayer().getId().equals(player.getId())))
                .toList();

        model.addAttribute("match", match);
        model.addAttribute("team1Players", team1Players);
        model.addAttribute("team2Players", team2Players);
        model.addAttribute("team1Stats", team1Stats);
        model.addAttribute("team2Stats", team2Stats);
        model.addAttribute("team1WithoutStats", team1WithoutStats);
        model.addAttribute("team2WithoutStats", team2WithoutStats);

        return "match/match-details";
    }


    @PostMapping("/{id}/submit-statistics")
    public String submitStatistics(@PathVariable long id, @RequestParam("playerIds[]") List<Long> playerIds,
                                   @RequestParam Map<String, String> stats, Model model) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            model.addAttribute("error", "Match not found.");
            return "error";
        }

        for (Long playerId : playerIds) {
            Player player = playerService.getPlayerById(playerId);
            if (player != null) {
                int pointsScored = Integer.parseInt(stats.get("pointsScored-" + playerId));
                int rebounds = Integer.parseInt(stats.get("rebounds-" + playerId));

                PlayerStatistics playerStats = new PlayerStatistics();
                playerStats.setMatch(match);
                playerStats.setPlayer(player);
                playerStats.setTeam(player.getTeam());
                playerStats.setPointsScored(pointsScored);
                playerStats.setRebounds(rebounds);

                playerStatisticsService.savePlayerStatistics(playerStats);  // Save the new statistics
            }
        }

        return "redirect:/match/" + id;  // Redirect to the match details page
    }

    @GetMapping("/{matchId}/edit-statistics/{statId}")
    public String editStatistics(@PathVariable long matchId, @PathVariable long statId, Model model) {
        PlayerStatistics stat = playerStatisticsService.getStatisticsById(statId);
        model.addAttribute("stat", stat);
        model.addAttribute("matchId", matchId);
        return "match/edit-statistics";  // Display edit form
    }

    @PostMapping("/{matchId}/edit-statistics/{statId}")
    public String updateStatistics(@PathVariable long matchId, @PathVariable long statId, PlayerStatistics updatedStat) {
        playerStatisticsService.updateStatistics(statId, updatedStat);
        return "redirect:/match/" + matchId;  // Redirect back to the match details page
    }

    @GetMapping("/{matchId}/delete-statistics/{statId}")
    public String deleteStatistics(@PathVariable long matchId, @PathVariable long statId) {
        playerStatisticsService.deleteStatistics(statId);
        return "redirect:/match/" + matchId;  // Redirect back to the match details page
    }


}

