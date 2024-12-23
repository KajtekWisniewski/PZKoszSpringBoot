package org.pzkosz.pzkosz.api;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.service.TeamService;
import org.pzkosz.pzkosz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEndpoint {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    /**
     * Get a list of all users.
     *
     * @return a list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<PZKoszUser>> getAllUsers() {
        List<PZKoszUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Delete a user by ID.
     *
     * @param id the ID of the user to delete
     * @return a success message
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been deleted.");
    }

    /**
     * Import teams from a CSV file.
     *
     * @param file the CSV file containing team data
     * @return a success or error message
     */
    @PostMapping("/teams/import")
    public ResponseEntity<String> importTeams(@RequestParam("file") MultipartFile file) {
        try {
            teamService.importTeamsFromCSV(file);
            return ResponseEntity.ok("Teams have been successfully imported.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to import teams: " + e.getMessage());
        }
    }

    /**
     * Export all teams to a CSV file.
     *
     * @return a ResponseEntity containing the CSV file as a stream
     */
    @GetMapping("/teams/export")
    public ResponseEntity<StreamingResponseBody> exportTeamsToCSV() {
        return teamService.exportTeamsToCSV();
    }
}
