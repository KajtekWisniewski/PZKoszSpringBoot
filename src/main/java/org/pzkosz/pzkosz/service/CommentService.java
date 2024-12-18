package org.pzkosz.pzkosz.service;

import org.pzkosz.pzkosz.model.Comment;
import org.pzkosz.pzkosz.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByMatchId(long matchId) {
        return commentRepository.findByMatchId(matchId);
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
