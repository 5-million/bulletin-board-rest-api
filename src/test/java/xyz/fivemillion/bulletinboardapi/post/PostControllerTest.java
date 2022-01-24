package xyz.fivemillion.bulletinboardapi.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import xyz.fivemillion.bulletinboardapi.config.web.PageRequest;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_OFFSET_VALUE;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_SIZE_VALUE;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

@WebMvcTest(PostController.class)
@Import(HttpEncodingAutoConfiguration.class)
class PostControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTokenUtil tokenUtil;

    private final Gson gson = new Gson();
    private final String AUTH_HEADER_NAME = "X-FM-AUTH";
    private final String TOKEN_PREFIX = "Bearer ";
    private final String BASE_URL = "/api/v1/posts";

    @Test
    @DisplayName("register fail: 인증되지 않은 사용자")
    void register_fail_인증되지않은사용자() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL))
                .andDo(print())
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
        result
                .andDo(print())
                .andExpect(status().isUnauthorized());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_USER, getError(result));
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
                .andExpect(jsonPath("$.response.commentCount").value("0"))
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
                .id(1L)
                .title("title1")
                .content("content1")
                .writer(user)
                .build();

        Post post2 = Post.builder()
                .id(2L)
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
                .andExpect(jsonPath("$.response[0].postId").value(post1.getId()))
                .andExpect(jsonPath("$.response[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$.response[0].writer").value(post1.getWriter().getDisplayName()))
                .andExpect(jsonPath("$.response[0].views").value(post1.getViews()))
                .andExpect(jsonPath("$.response[0].commentCount").value(post1.getComments().size()))
                .andExpect(jsonPath("$.response[1].postId").value(post2.getId()))
                .andExpect(jsonPath("$.response[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$.response[1].writer").value(post2.getWriter().getDisplayName()))
                .andExpect(jsonPath("$.response[1].views").value(post2.getViews()))
                .andExpect(jsonPath("$.response[1].commentCount").value(post2.getComments().size()));
    }

    @Test
    @DisplayName("getById fail: 잘못된 형식의 PathVariable")
    void getById_fail_illegalPathVariable() throws Exception {
        //given
        RequestBuilder requestBuilders = MockMvcRequestBuilders.get(BASE_URL + "/asd");

        //when
        ResultActions result = mvc.perform(requestBuilders);

        //then
        result.andDo(print())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("getById"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("getById fail: 포스트 ID < 1")
    void getById_fail_postIdLessThan1() throws Exception {
        //given
        RequestBuilder requestBuilder= MockMvcRequestBuilders.get(BASE_URL + "/-1");

        //when
        ResultActions result = mvc.perform(requestBuilder);

        //then
        result.andDo(print())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("getById"))
                .andExpect(status().isNotFound());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.POST_NOT_FOUND, getError(result));
    }

    @Test
    @DisplayName("getById fail: 존재하지 않는 포스트")
    void getById_fail_존재하지않는포스트() throws Exception {
        //given
        String url = BASE_URL + "/1";
        given(postService.findById(anyLong())).willThrow(new NotFoundException(Error.POST_NOT_FOUND, HttpStatus.NOT_FOUND));

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("getById"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Error.POST_NOT_FOUND.getMessage()));

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.POST_NOT_FOUND, getError(result));
    }

    @Test
    @DisplayName("getById success")
    void getById_success() throws Exception {
        //given
        String url = BASE_URL + "/1";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(writer)
                .build();

        given(postService.findById(anyLong())).willReturn(post);

        //when
        ResultActions result = mvc.perform(requestBuilder);

        //then
        verify(postService, times(1)).increaseView(any(Post.class));
        result.andDo(print())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("getById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.postId").value(post.getId()))
                .andExpect(jsonPath("$.response.title").value(post.getTitle()))
                .andExpect(jsonPath("$.response.content").value(post.getContent()))
                .andExpect(jsonPath("$.response.writer").value(writer.getDisplayName()))
                .andExpect(jsonPath("$.response.views").value(post.getViews()))
                .andExpect(jsonPath("$.response.commentCount").value(post.getComments().size()))
                .andExpect(jsonPath("$.response.comments").isArray());
    }

    @Test
    @DisplayName("delete fail: 인증되지 않은 사용자")
    void delete_fail_unauthorized() throws Exception {
        //given
        String url = BASE_URL + "/1";

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete(url));

        //then
        result.andExpect(status().isUnauthorized());
    }

    private ResultActions performDelete(Long id, String token) throws Exception {
        final String url = BASE_URL + "/" + id;

        return mvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());
    }

    @Test
    @DisplayName("delete fail: 존재하지 않는 사용자")
    void delete_fail_존재하지않는사용자() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = performDelete(1L, token);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isUnauthorized());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_USER, getError(result));
    }

    @Test
    @DisplayName("delete fail: 잘못된 형식의 PathVariable")
    void delete_fail_illegalPathVariable() throws Exception {
        //given
        final String url = BASE_URL + "/asd";

        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("delete fail: 포스트 ID < 1")
    void delete_fail_postIdLessThan1() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);

        //when
        ResultActions result = performDelete(0L, token);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isBadRequest());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_POST, getError(result));
    }

    @Test
    @DisplayName("delete fail: 존재하지 않는 포스트")
    void delete_fail_존재하지않는포스트() throws Exception {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(writer);

        given(userService.findByEmail(anyString())).willReturn(writer);
        doThrow(new NotFoundException(Error.POST_NOT_FOUND, HttpStatus.NOT_FOUND))
                .when(postService).delete(writer, 1L);

        //when
        ResultActions result = performDelete(1L, token);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isBadRequest());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_POST, getError(result));
    }

    @Test
    @DisplayName("delete fail: Forbidden(포스트 작성자가 아님)")
    void delete_fail_forbidden() throws Exception {
        //given
        User requester = User.builder()
                .email("def@test.com")
                .displayName("display name1")
                .build();

        String token = tokenUtil.generateJwtToken(requester);

        given(userService.findByEmail(anyString())).willReturn(requester);
        doThrow(new NotOwnerException(Error.NOT_POST_OWNER))
                .when(postService).delete(requester, 1L);

        //when
        ResultActions result = performDelete(1L, token);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden());

        assertEquals(NotOwnerException.class, getException(result).getClass());
        assertEquals(Error.NOT_POST_OWNER, getError(result));
    }

    @Test
    @DisplayName("delete success")
    void delete_success() throws Exception {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(writer)
                .build();

        String token = tokenUtil.generateJwtToken(writer);

        given(userService.findByEmail(anyString())).willReturn(writer);
        given(postService.findById(anyLong())).willReturn(post);

        //when
        ResultActions result = performDelete(1L, token);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("search: q=null")
    void search_qIsNull() throws Exception {
        //given
        String url = BASE_URL + "/search";
        given(postService.findAll(any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(url)).andDo(print());

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("search: (offset=null, size=null)")
    void search_null_null() throws Exception {
        //given
        String url = BASE_URL + "/search";
        String query = "query";

        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(url).param("q", query)).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(DEFAULT_OFFSET_VALUE, pageableCaptor.getValue().getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search: (offset=5, size=null)")
    void search_5_null() throws Exception {
        //given
        String url = BASE_URL + "/search";
        String query = "query"; String offset = "5";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("q", query);
        params.add("offset", offset);

        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .params(params)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(Long.parseLong(offset), pageableCaptor.getValue().getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search: (offset=null, size=5)")
    void search_null_5() throws Exception {
        //given
        String url = BASE_URL + "/search";
        String query = "query"; String size = "5";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("q", query);
        params.add("size", size);

        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .params(params)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(DEFAULT_OFFSET_VALUE, pageableCaptor.getValue().getOffset());
        assertEquals(Long.parseLong(size), pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search: (offset=5, size=5)")
    void search_5_5() throws Exception {
        //given
        String url = BASE_URL + "/search";
        String query = "query"; String offset = "5"; String size = "5";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("q", query);
        params.add("offset", offset);
        params.add("size", size);

        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .params(params)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(Long.parseLong(offset), pageableCaptor.getValue().getOffset());
        assertEquals(Long.parseLong(size), pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search: (offset<0, size<0)")
    void search_negative_negative() throws Exception {
        //given
        String url = BASE_URL + "/search";
        String query = "query"; String offset = "-1"; String size = "-1";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("q", query);
        params.add("offset", offset);
        params.add("size", size);

        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .params(params)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(DEFAULT_OFFSET_VALUE, pageableCaptor.getValue().getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search: empty")
    void search_success_result_empty() throws Exception {
        //given
        String q = "ab";
        String url = BASE_URL + "/search";
        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("q", q)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(q, queryCaptor.getValue());
        assertEquals(DEFAULT_OFFSET_VALUE, pageableCaptor.getValue().getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageableCaptor.getValue().getSize());
    }

    @Test
    @DisplayName("search success: exist")
    void search_success_result_exist() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post1 = Post.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .writer(user)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("title2")
                .content("content2")
                .writer(user)
                .build();

        List<Post> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);

        String url = BASE_URL + "/search";
        String query = "title";
        given(postService.findByQuery(anyString(), any(Pageable.class))).willReturn(posts);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("q", query)
        ).andDo(print());
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        //then
        result
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].postId").value(post1.getId()))
                .andExpect(jsonPath("$.response[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$.response[0].writer").value(user.getDisplayName()))
                .andExpect(jsonPath("$.response[0].views").value(post1.getViews()))
                .andExpect(jsonPath("$.response[0].commentCount").value(post1.getComments().size()))
                .andExpect(jsonPath("$.response[1].postId").value(post2.getId()))
                .andExpect(jsonPath("$.response[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$.response[1].writer").value(user.getDisplayName()))
                .andExpect(jsonPath("$.response[1].views").value(post2.getViews()))
                .andExpect(jsonPath("$.response[1].commentCount").value(post2.getComments().size()));

        verify(postService, times(1)).findByQuery(queryCaptor.capture(), pageableCaptor.capture());
        assertEquals(query, queryCaptor.getValue());
        assertEquals(DEFAULT_OFFSET_VALUE, pageableCaptor.getValue().getOffset());
        assertEquals(DEFAULT_SIZE_VALUE, pageableCaptor.getValue().getSize());
    }
}