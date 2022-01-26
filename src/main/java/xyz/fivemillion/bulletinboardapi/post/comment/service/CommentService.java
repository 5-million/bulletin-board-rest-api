package xyz.fivemillion.bulletinboardapi.post.comment.service;

import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

public interface CommentService {

    Comment register(User writer, Post post, String content) throws Exception;
    void delete(User requester, long commentId);
}
