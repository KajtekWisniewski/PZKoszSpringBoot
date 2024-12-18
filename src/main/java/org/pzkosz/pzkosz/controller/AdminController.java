package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.service.UserService;
import org.pzkosz.pzkosz.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        List<PZKoszUser> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    @GetMapping("/delete/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/import/teams")
    public String importTeams(@RequestParam("file") MultipartFile file) {
        try {
            teamService.importTeamsFromCSV(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/export/teams")
    public ResponseEntity<StreamingResponseBody> exportTeamsToCSV() {
        return teamService.exportTeamsToCSV();
    }
}
