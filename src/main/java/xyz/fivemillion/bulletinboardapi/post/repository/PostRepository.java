package xyz.fivemillion.bulletinboardapi.post.repository;

import xyz.fivemillion.bulletinboardapi.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void save(Post post);
    Optional<Post> findById(long id);
    List<Post> findAll(long offset, long size);
    List<Post> findByQuery(String q, long offset, long size);
    void delete(Post post);
}
