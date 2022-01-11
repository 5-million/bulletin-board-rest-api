package xyz.fivemillion.bulletinboardapi.post.repository;

import xyz.fivemillion.bulletinboardapi.post.Post;

import java.util.List;

public interface PostRepository {

    void save(Post post);
    List<Post> findAll(long offset, long size);
    List<Post> findByWriter(Long writerId);
}
