package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.*;
import org.pzkosz.pzkosz.repository.UserRepository;
import org.pzkosz.pzkosz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchStatsService matchStatsService;

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
            return "match/add";
        }

        Match match = matchService.createMatch(team1Id, team2Id, matchDate);

        return "redirect:/match/" + match.getId();
    }

    @GetMapping("/archive")
    public String viewMatchArchive(Model model) {
        Date now = new Date();
        List<Match> pastMatches = matchService.getMatchesBeforeDate(now);

        pastMatches.sort((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()));

        model.addAttribute("matches", pastMatches);
        model.addAttribute("title", "Match Archive");
        return "match/match-archive";
    }

    @GetMapping("/schedule")
    public String viewMatchSchedule(Model model) {
        Date now = new Date();
        List<Match> upcomingMatches = matchService.getMatchesAfterDate(now);

        upcomingMatches.sort((m1, m2) -> m1.getMatchDate().compareTo(m2.getMatchDate()));

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

        int team1Score = matchService.getTeamScore(match.getId(), match.getTeam1().getId());
        int team2Score = matchService.getTeamScore(match.getId(), match.getTeam2().getId());

        if (match.getTeam1Score() != team1Score || match.getTeam2Score() != team2Score) {
            match.setTeam1Score(team1Score);
            match.setTeam2Score(team2Score);
            matchService.updateMatchScores(match.getId());
        }

        List<Player> team1Players = playerService.getPlayersByTeam(match.getTeam1().getId());
        List<Player> team2Players = playerService.getPlayersByTeam(match.getTeam2().getId());

        List<PlayerStatistics> team1Stats = playerStatisticsService.findByMatchAndTeam(id, match.getTeam1());
        List<PlayerStatistics> team2Stats = playerStatisticsService.findByMatchAndTeam(id, match.getTeam2());

        List<Player> team1WithoutStats = team1Players.stream()
                .filter(player -> team1Stats.stream().noneMatch(stat -> stat.getPlayer().getId().equals(player.getId())))
                .toList();

        List<Player> team2WithoutStats = team2Players.stream()
                .filter(player -> team2Stats.stream().noneMatch(stat -> stat.getPlayer().getId().equals(player.getId())))
                .toList();

        List<Comment> comments = commentService.getCommentsByMatchId(id);
        model.addAttribute("comments", comments);

        model.addAttribute("match", match);
        model.addAttribute("team1Players", team1Players);
        model.addAttribute("team2Players", team2Players);
        model.addAttribute("team1Stats", team1Stats);
        model.addAttribute("team2Stats", team2Stats);
        model.addAttribute("team1WithoutStats", team1WithoutStats);
        model.addAttribute("team2WithoutStats", team2WithoutStats);
        model.addAttribute("team1Score", team1Score);
        model.addAttribute("team2Score", team2Score);

        return "match/match-details";
    }

    @PostMapping("/{id}/add-comment")
    public String addComment(@PathVariable long id, @RequestParam String content, Model model) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            model.addAttribute("error", "Match not found.");
            return "error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        PZKoszUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setMatch(match);
        comment.setContent(content);
        comment.setCreatedAt(new java.util.Date());
        comment.setUser(currentUser);

        commentService.addComment(comment);
        return "redirect:/match/" + id;
    }


    @PostMapping("/{matchId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable long matchId, @PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/match/{matchId}";
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

                playerStatisticsService.savePlayerStatistics(playerStats);
            }
        }

        matchStatsService.updateTeamWinsAndLosses(match.getTeam1().getId());
        matchStatsService.updateTeamWinsAndLosses(match.getTeam2().getId());

        return "redirect:/match/" + id;
    }

    @GetMapping("/{matchId}/edit-statistics/{statId}")
    public String editStatistics(@PathVariable long matchId, @PathVariable long statId, Model model) {
        PlayerStatistics stat = playerStatisticsService.getStatisticsById(statId);
        model.addAttribute("stat", stat);
        model.addAttribute("matchId", matchId);
        return "match/edit-statistics";
    }

    @PostMapping("/{matchId}/edit-statistics/{statId}")
    public String updateStatistics(@PathVariable long matchId, @PathVariable long statId, PlayerStatistics updatedStat) {
        playerStatisticsService.updateStatistics(statId, updatedStat);
        return "redirect:/match/" + matchId;
    }

    @GetMapping("/{matchId}/delete-statistics/{statId}")
    public String deleteStatistics(@PathVariable long matchId, @PathVariable long statId) {
        playerStatisticsService.deleteStatistics(statId);
        return "redirect:/match/" + matchId;
    }

}

