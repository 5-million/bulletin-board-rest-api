package xyz.fivemillion.bulletinboardapi.post.category.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.post.category.repository.PostCategoryRepository;

@ExtendWith(MockitoExtension.class)
abstract class PostCategoryServiceImplTest {

    @Mock protected PostCategoryRepository postCategoryRepository;
    @InjectMocks protected PostCategoryServiceImpl postCategoryService;
}