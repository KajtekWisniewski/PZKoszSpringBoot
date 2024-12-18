package org.pzkosz.pzkosz.controller;


import org.pzkosz.pzkosz.model.Match;
import org.pzkosz.pzkosz.model.Player;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.MatchService;
import org.pzkosz.pzkosz.service.PlayerService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @GetMapping("/")
    public String showSearchPage() {
        return "search";
    }

    @PostMapping
    public String search(@RequestParam("query") String query, Model model) {

        List<Player> players = playerService.searchPlayers(query);
        List<Team> teams = teamService.searchTeams(query);

        List<Match> matches = matchService.searchMatches(query);

        model.addAttribute("players", players);
        model.addAttribute("teams", teams);
        model.addAttribute("matches", matches);
        model.addAttribute("query", query);

        return "search";
    }
}

