package xyz.fivemillion.bulletinboardapi.post.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.PostController;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean protected UserService userService;
    @MockBean protected PostService postService;
    @Autowired protected MockMvc mvc;
    @Autowired protected JwtTokenUtil tokenUtil;

    protected final Gson gson = new Gson();
    protected final String AUTH_HEADER_NAME = "X-FM-AUTH";
    protected final String TOKEN_PREFIX = "Bearer ";
    protected final String BASE_URL = "/api/v1/posts";
}