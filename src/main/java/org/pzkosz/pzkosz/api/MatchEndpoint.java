package org.pzkosz.pzkosz.api;

import org.pzkosz.pzkosz.dto.MatchStatisticsDTO;
import org.pzkosz.pzkosz.dto.PlayerStatisticsDTO;
import org.pzkosz.pzkosz.model.*;
import org.pzkosz.pzkosz.repository.MatchRepository;
import org.pzkosz.pzkosz.repository.UserRepository;
import org.pzkosz.pzkosz.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchEndpoint {

    private final MatchService matchService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final PlayerStatisticsService playerStatisticsService;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final MatchStatsService matchStatsService;

    public MatchEndpoint(MatchService matchService, TeamService teamService, PlayerService playerService,
                         PlayerStatisticsService playerStatisticsService, CommentService commentService,
                         UserRepository userRepository, MatchStatsService matchStatsService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.playerService = playerService;
        this.playerStatisticsService = playerStatisticsService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.matchStatsService = matchStatsService;
    }

    @PostMapping
    public ResponseEntity<?> createMatch(@RequestParam("team1") Long team1Id,
                                         @RequestParam("team2") Long team2Id,
                                         @RequestParam("matchDate") String matchDate) {
        if (team1Id.equals(team2Id)) {
            return ResponseEntity.badRequest().body("The teams cannot be the same.");
        }
        Match match = matchService.createMatch(team1Id, team2Id, matchDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @GetMapping("/archive")
    public ResponseEntity<List<Match>> viewMatchArchive() {
        List<Match> pastMatches = matchService.getMatchesBeforeDate(new Date());
        pastMatches.sort((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()));
        return ResponseEntity.ok(pastMatches);
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<Match>> viewMatchSchedule() {
        List<Match> upcomingMatches = matchService.getMatchesAfterDate(new Date());
        upcomingMatches.sort((m1, m2) -> m1.getMatchDate().compareTo(m2.getMatchDate()));
        return ResponseEntity.ok(upcomingMatches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewMatchDetails(@PathVariable long id) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        int team1Score = matchService.getTeamScore(match.getId(), match.getTeam1().getId());
        int team2Score = matchService.getTeamScore(match.getId(), match.getTeam2().getId());
        match.setTeam1Score(team1Score);
        match.setTeam2Score(team2Score);
        matchService.updateMatchScores(match.getId());
        matchStatsService.updateTeamWinsAndLosses(match.getTeam1().getId());
        matchStatsService.updateTeamWinsAndLosses(match.getTeam2().getId());

        return ResponseEntity.ok(match);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> viewMatchComments(@PathVariable long id) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Comment> comments = commentService.getCommentsByMatchId(id);
        return ResponseEntity.ok(comments);

    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable long id, @RequestBody String content) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        PZKoszUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setMatch(match);
        comment.setContent(content);
        comment.setCreatedAt(new Date());
        comment.setUser(currentUser);

        commentService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @DeleteMapping("/{matchId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long matchId, @PathVariable long commentId) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/statistics")
    public ResponseEntity<?> submitStatistics(@PathVariable long id,
                                              @RequestBody MatchStatisticsDTO statisticsDTO) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        for (PlayerStatisticsDTO statDTO : statisticsDTO.getPlayerStatistics()) {
            Player player = playerService.getPlayerById(statDTO.getPlayerId());
            if (player != null) {
                PlayerStatistics playerStats = new PlayerStatistics();
                playerStats.setMatch(match);
                playerStats.setPlayer(player);
                playerStats.setTeam(player.getTeam());
                playerStats.setPointsScored(statDTO.getPointsScored());
                playerStats.setRebounds(statDTO.getRebounds());

                playerStatisticsService.savePlayerStatistics(playerStats);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Statistics submitted successfully");
    }

    @PutMapping("/{matchId}/statistics/{statId}")
    public ResponseEntity<?> updateStatistics(@PathVariable long matchId,
                                              @PathVariable long statId,
                                              @RequestBody PlayerStatisticsDTO statisticsDTO) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        Player player = playerService.getPlayerById(statisticsDTO.getPlayerId());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found.");
        }

        PlayerStatistics updatedStats = new PlayerStatistics();
        updatedStats.setId(statId);
        updatedStats.setMatch(match);
        updatedStats.setPlayer(player);
        updatedStats.setTeam(player.getTeam());
        updatedStats.setPointsScored(statisticsDTO.getPointsScored());
        updatedStats.setRebounds(statisticsDTO.getRebounds());

        playerStatisticsService.updateStatistics(statId, updatedStats);
        return ResponseEntity.ok("Statistics updated successfully");
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> getMatchStatistics(@PathVariable long id) {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        List<PlayerStatistics> statistics = playerStatisticsService.getStatisticsByMatchId(id);
        return ResponseEntity.ok(statistics);
    }

    @DeleteMapping("/{matchId}/statistics/{statId}")
    public ResponseEntity<?> deleteStatistics(@PathVariable long matchId, @PathVariable long statId) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        playerStatisticsService.deleteStatistics(statId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
