package xyz.fivemillion.bulletinboardapi.post.comment.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.post.comment.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock protected CommentRepository commentRepository;
    @InjectMocks protected CommentServiceImpl commentService;
}