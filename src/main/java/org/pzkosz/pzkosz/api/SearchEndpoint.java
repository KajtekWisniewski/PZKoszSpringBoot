package org.pzkosz.pzkosz.api;

import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.MatchService;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchEndpoint {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final MatchService matchService;

    public SearchEndpoint(PlayerService playerService, TeamService teamService, MatchService matchService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.matchService = matchService;
    }

    /**
     * Search for players, teams, and matches by a query string.
     *
     * @param query the search query
     * @return a ResponseEntity containing the search results
     */
    @GetMapping
    public ResponseEntity<?> search(@RequestParam("query") String query) {
        List<Player> players = playerService.searchPlayers(query);
        List<Team> teams = teamService.searchTeams(query);
        List<Match> matches = matchService.searchMatches(query);

        return ResponseEntity.ok(new SearchResults(players, teams, matches));
    }

        private record SearchResults(List<Player> players, List<Team> teams, List<Match> matches) {
    }
}
