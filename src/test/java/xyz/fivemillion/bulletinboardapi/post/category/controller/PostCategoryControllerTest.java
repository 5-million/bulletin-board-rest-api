package xyz.fivemillion.bulletinboardapi.post.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategoryController;
import xyz.fivemillion.bulletinboardapi.post.category.service.PostCategoryService;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

@WebMvcTest(PostCategoryController.class)
abstract class PostCategoryControllerTest {

    @MockBean protected PostCategoryService postCategoryService;
    @MockBean protected UserService userService;
    @Autowired protected MockMvc mvc;

    protected static final String BASE_URL = "/api/v1/category";
}