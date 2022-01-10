package xyz.fivemillion.bulletinboardapi.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean private UserService userService;
    @MockBean private PostService postService;
    @Autowired private MockMvc mvc;
    @Autowired private JwtTokenUtil tokenUtil;

    private static final Gson gson = new Gson();

    private static final String AUTH_HEADER_NAME = "X-FM-AUTH";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BASE_URL = "/api/v1";
    private static final String REGISTER_URL = BASE_URL + "/posts";

    @Test
    @DisplayName("register fail: 인증되지 않은 사용자")
    void register_fail_인증되지않은사용자() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(REGISTER_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("register fail: 존재하지 않는 사용자")
    void register_fail_존재하지않는사용자() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(REGISTER_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: title is blank")
    void register_fail_titleIsBlank() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("", "content");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(REGISTER_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: 제목 길이제한 초과")
    void register_fail_제목길이제한초과() throws Exception {
        //given
        String title = "titletitletitletitletitletitletitletitleti" +
                "tletitletitletitletitletitletitletitletitletitletitletitlet" +
                "itletitletitletitletitletitletitletitletitletitle";

        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest(title, "content");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(REGISTER_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: content is blank")
    void register_fail_contentIsBlank() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(REGISTER_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        given(userService.findByEmail(anyString())).willReturn(user);
        given(postService.register(any(User.class), any(PostRegisterRequest.class))).willReturn(
                Post.builder()
                        .title(request.getTitle())
                        .content(request.getTitle())
                        .writer(user)
                        .build()
        );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(REGISTER_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("title"))
                .andExpect(jsonPath("$.response.writer").value("display name"))
                .andExpect(jsonPath("$.response.views").value("0"))
                .andExpect(jsonPath("$.response.commentsCount").value("0"))
                .andExpect(jsonPath("$.response.createAt").exists())
                .andExpect(jsonPath("$.response.updateAt").exists());
    }
}