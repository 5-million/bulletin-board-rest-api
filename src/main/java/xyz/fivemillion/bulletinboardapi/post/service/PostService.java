package xyz.fivemillion.bulletinboardapi.post.service;

import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.User;

public interface PostService {

    Post register(User writer, PostRegisterRequest request);
}
