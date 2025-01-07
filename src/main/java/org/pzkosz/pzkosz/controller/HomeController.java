package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.service.MatchStatsService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final TeamService teamService;
    private final MatchStatsService matchStatsService;

    public HomeController(TeamService teamService, MatchStatsService matchStatsService) {

        this.teamService = teamService;
        this.matchStatsService = matchStatsService;
    }

    @GetMapping("/")
    public String viewAllTeams(Model model) {
        matchStatsService.updateAllTeamsWinsAndLosses();
        List<Team> teams = teamService.getAllTeams();

        List<Team> sortedTeams = teams.stream()
                .sorted((t1, t2) -> Integer.compare(t2.getWins(), t1.getWins()))
                .toList();

        model.addAttribute("teams", sortedTeams);
        return "home";
    }

}
