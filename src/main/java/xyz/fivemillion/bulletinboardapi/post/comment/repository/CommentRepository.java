package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import xyz.fivemillion.bulletinboardapi.post.comment.Comment;

import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);
    Optional<Comment> findById(long id);
    void delete(Comment comment);
}
