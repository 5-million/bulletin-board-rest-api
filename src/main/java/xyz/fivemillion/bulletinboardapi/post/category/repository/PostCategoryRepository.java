package xyz.fivemillion.bulletinboardapi.post.category.repository;

import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.List;
import java.util.Optional;

public interface PostCategoryRepository {

    void save(PostCategory postCategory);
    Optional<PostCategory> findById(Long id);
    Optional<PostCategory> findByName(String name);
    List<PostCategory> findAll();
}
