package xyz.fivemillion.bulletinboardapi.post.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.category.repository.PostCategoryRepository;

import java.util.List;

import static xyz.fivemillion.bulletinboardapi.utils.CheckUtil.checkNotNull;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCategoryServiceImpl implements PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    @Override
    @Transactional
    public PostCategory register(PostCategoryRegisterRequest request) {
        checkNotNull(request, Error.REQUEST_DTO_IS_NULL);

        if (isExist(request.getCategoryName())) throw new DuplicateException(Error.CATEGORY_DUPLICATE);

        PostCategory parent = null;
        if (request.getParentId() != null) {
            parent = postCategoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException(Error.UNKNOWN_CATEGORY));
        }

        PostCategory newCategory = PostCategory.builder()
                .categoryName(request.getCategoryName())
                .parent(parent)
                .build();

        postCategoryRepository.save(newCategory);
        return newCategory;
    }

    @Override
    public boolean isExist(String categoryName) {
        try {
            findOne(categoryName);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public List<PostCategory> findAll() {
        return postCategoryRepository.findAll();
    }

    @Override
    public PostCategory findOne(Long categoryId) {
        checkNotNull(categoryId, Error.ARGUMENT_IS_NULL);
        return postCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Error.CATEGORY_NOT_FOUND));
    }

    @Override
    public PostCategory findOne(String categoryName) {
        checkNotNull(categoryName, Error.ARGUMENT_IS_NULL);
        return postCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NotFoundException(Error.CATEGORY_NOT_FOUND));
    }
}
