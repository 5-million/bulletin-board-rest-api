package xyz.fivemillion.bulletinboardapi.post.comment.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.comment.CommentController;
import xyz.fivemillion.bulletinboardapi.post.comment.service.CommentService;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean protected CommentService commentService;
    @MockBean protected UserService userService;
    @MockBean protected PostService postService;
    @Autowired protected JwtTokenUtil tokenUtil;
    @Autowired protected MockMvc mvc;

    protected final Gson gson = new Gson();
    protected final String TOKEN_HEADER_NAME = "X-FM-AUTH";
    protected final String TOKEN_PREFIX = "Bearer ";
    protected final User requester = User.builder()
            .email("abc@test.com")
            .displayName("display name")
            .build();
}