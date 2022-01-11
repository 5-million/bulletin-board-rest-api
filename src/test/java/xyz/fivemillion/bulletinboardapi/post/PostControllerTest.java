package xyz.fivemillion.bulletinboardapi.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.config.web.PageRequest;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_OFFSET_VALUE;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_SIZE_VALUE;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTokenUtil tokenUtil;

    private static final Gson gson = new Gson();

    private static final String AUTH_HEADER_NAME = "X-FM-AUTH";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BASE_URL = "/api/v1/posts";

    @Test
    @DisplayName("register fail: 인증되지 않은 사용자")
    void register_fail_인증되지않은사용자() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL))
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
                MockMvcRequestBuilders.post(BASE_URL)
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
                MockMvcRequestBuilders.post(BASE_URL)
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
                MockMvcRequestBuilders.post(BASE_URL)
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
                MockMvcRequestBuilders.post(BASE_URL)
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
                MockMvcRequestBuilders.post(BASE_URL)
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

    @Test
    @DisplayName("findAll (offset=null, size=null)")
    void findAll_success_1() throws Exception {
        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(BASE_URL));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(PageRequest.class);

        //then
        result.andExpect(status().isOk());
        verify(postService, times(1)).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(DEFAULT_OFFSET_VALUE, pageable.getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageable.getSize());
    }

    @Test
    @DisplayName("findAll (offset<0, size<0)")
    void findAll_success_2() throws Exception {
        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL)
                        .param("offset", "-1")
                        .param("size", "-1")
        );
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(PageRequest.class);

        //then
        result.andExpect(status().isOk());
        verify(postService, times(1)).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(DEFAULT_OFFSET_VALUE, pageable.getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageable.getSize());
    }

    @Test
    @DisplayName("findAll (offset=5, size=null)")
    void findAll_success_3() throws Exception {
        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL)
                        .param("offset", "5")
        );
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(PageRequest.class);

        //then
        result.andExpect(status().isOk());
        verify(postService, times(1)).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(5L, pageable.getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageable.getSize());
    }

    @Test
    @DisplayName("findAll (offset=null, size=5)")
    void findAll_success_4() throws Exception {
        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL)
                        .param("size", "5")
        );
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(PageRequest.class);

        //then
        result.andExpect(status().isOk());
        verify(postService, times(1)).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(DEFAULT_OFFSET_VALUE, pageable.getOffset());
        assertEquals(5L, pageable.getSize());
    }

    @Test
    @DisplayName("findAll (offset=5, size=5)")
    void findAll_success_5() throws Exception {
        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL)
                        .param("offset", "5")
                        .param("size", "5")
        );
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(PageRequest.class);

        //then
        result.andExpect(status().isOk());
        verify(postService, times(1)).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(5L, pageable.getOffset());
        assertEquals(5L, pageable.getSize());
    }

    @Test
    @DisplayName("findAll response test")
    void findAll_success_response_test() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post1 = Post.builder()
                .title("title1")
                .content("content1")
                .writer(user)
                .build();

        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .writer(user)
                .build();
        List<Post> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);

        given(postService.findAll(any(PageRequest.class))).willReturn(posts);

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(BASE_URL));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].title").value("title1"))
                .andExpect(jsonPath("$.response[0].writer").value("display name"))
                .andExpect(jsonPath("$.response[0].views").value("0"))
                .andExpect(jsonPath("$.response[0].commentsCount").value("0"))
                .andExpect(jsonPath("$.response[1].title").value("title2"))
                .andExpect(jsonPath("$.response[1].writer").value("display name"))
                .andExpect(jsonPath("$.response[1].views").value("0"))
                .andExpect(jsonPath("$.response[1].commentsCount").value("0"));
    }
}