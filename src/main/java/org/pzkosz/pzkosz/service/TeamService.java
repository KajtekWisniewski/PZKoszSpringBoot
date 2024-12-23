package org.pzkosz.pzkosz.service;

import com.opencsv.CSVWriter;
import org.pzkosz.pzkosz.model.Team;
import org.pzkosz.pzkosz.repository.TeamRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeamById(Long id) {
        teamRepository.deleteById(id);
    }

    public List<Team> searchTeams(String query) {
        return teamRepository.findByNameRegex(query);
    }

    public int importTeamsFromCSV(MultipartFile file) throws IOException {
        List<Team> teams = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("File is empty");
            }

            while ((line = reader.readLine()) != null) {
                String[] data = parseCSVLine(line);

                if (data.length < 3) {
                    continue;
                }

                Team team = new Team();
                team.setName(data[0].trim());
                team.setCity(data[1].trim());
                team.setDescription(data.length > 2 ? data[2].trim() : "");
                teams.add(team);
            }
        }

        Map<String, Team> uniqueTeams = new HashMap<>();
        for (Team team : teams) {
            uniqueTeams.put(team.getName(), team);
        }

        List<Team> teamsToSave = new ArrayList<>(uniqueTeams.values());
        teamRepository.saveAll(teamsToSave);
        return teamsToSave.size();
    }

    public ResponseEntity<StreamingResponseBody> exportTeamsToCSV() {
        List<Team> teams = teamRepository.findAll();

        StreamingResponseBody responseBody = outputStream -> {
            try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                writer.writeNext(new String[]{"Team Name", "City", "Description"});

                for (Team team : teams) {
                    writer.writeNext(new String[]{
                            team.getName(),
                            team.getCity(),
                            team.getDescription()
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teams.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(responseBody);
    }

    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }
}
