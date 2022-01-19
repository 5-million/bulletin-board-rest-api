package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import xyz.fivemillion.bulletinboardapi.post.comment.Comment;

public interface CommentRepository {

    void save(Comment comment);
}
