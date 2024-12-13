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

//    @Query("SELECT m FROM Match m WHERE m.matchDate = :date OR m.team1.name LIKE %:pattern% OR m.team2.name LIKE %:pattern%")
//    List<Match> findByDateOrTeamsRegex(@Param("date") Date date, @Param("pattern") String pattern);


}
