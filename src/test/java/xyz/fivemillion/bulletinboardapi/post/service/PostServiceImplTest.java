package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock protected PostRepository postRepository;
    @InjectMocks protected PostServiceImpl postService;
}