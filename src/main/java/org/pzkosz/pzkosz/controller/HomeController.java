package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {

    private final TeamService teamService;

    public HomeController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/")
    public String listTeamsOnHomePage(Model model) {
        System.out.println("Loading homepage with teams...");
        model.addAttribute("teams", teamService.getAllTeams());
        return "home";
    }

}
