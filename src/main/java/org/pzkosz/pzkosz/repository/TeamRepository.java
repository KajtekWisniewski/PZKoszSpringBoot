package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t WHERE t.name LIKE %:pattern%")
    List<Team> findByNameRegex(@Param("pattern") String pattern);
}
