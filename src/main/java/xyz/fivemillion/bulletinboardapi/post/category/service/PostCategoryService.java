package xyz.fivemillion.bulletinboardapi.post.category.service;

import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryRegisterRequest;

import java.util.List;

public interface PostCategoryService {

    PostCategory register(PostCategoryRegisterRequest request);
    List<PostCategory> findAll();
    PostCategory findOne(Long categoryId);
    PostCategory findOne(String categoryName);
    boolean isExist(String categoryName);
}
