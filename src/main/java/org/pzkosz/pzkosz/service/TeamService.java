package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        return team.orElse(null);
    }

    public void saveTeam(Team team) {
        teamRepository.save(team);
    }

    public void deleteTeamById(Long id) {
        teamRepository.deleteById(id);
    }

    public List<Team> searchTeams(String query) {
        return teamRepository.findByNameRegex(query);
    }

    public void importTeamsFromCSV(MultipartFile file) throws IOException {
        List<Team> teams = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            Team team = new Team();
            team.setName(data[0]); // Assuming first column is the team name
            team.setCity(data[1]);
            team.setDescription(data[2]);
            teams.add(team);
        }

        teamRepository.saveAll(teams);
    }

    public StreamingResponseBody exportTeamsToCSV() {
        List<Team> teams = teamRepository.findAll();

        return outputStream -> {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                writer.write("Team Name\n");

                for (Team team : teams) {
                    writer.write(team.getName() + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
