package org.pzkosz.pzkosz.controller;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.model.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new PZKoszUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(PZKoszUser user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
