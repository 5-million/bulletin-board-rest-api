package xyz.fivemillion.bulletinboardapi.post.service;

import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.List;

public interface PostService {

    Post register(User writer, PostRegisterRequest request);
    List<Post> findAll(Pageable pageable);
    Post findById(Long id);
    void delete(User writer, Long postId);
}
