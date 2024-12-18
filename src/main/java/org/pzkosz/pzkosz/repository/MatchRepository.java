package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByMatchDateBefore(Date date);

    List<Match> findByMatchDateAfter(Date date);

    @Query("SELECT m FROM Match m WHERE m.team1.id = :teamId OR m.team2.id = :teamId")
    List<Match> findMatchesByTeamId(@Param("teamId") long teamId);

    List<Match> findByMatchDateOrTeam1NameContainingIgnoreCaseOrTeam2NameContainingIgnoreCase(Date matchDate, String team1Name, String team2Name);

    List<Match> findByTeam1NameContainingIgnoreCaseOrTeam2NameContainingIgnoreCase(String team1Name, String team2Name);
}
