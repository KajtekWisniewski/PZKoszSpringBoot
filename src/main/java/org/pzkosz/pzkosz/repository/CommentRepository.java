package org.pzkosz.pzkosz.repository;

import org.pzkosz.pzkosz.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMatchId(long matchId);
}
