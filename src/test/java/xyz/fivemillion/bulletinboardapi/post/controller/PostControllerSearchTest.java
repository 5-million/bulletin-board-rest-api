package xyz.fivemillion.bulletinboardapi.post.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import xyz.fivemillion.bulletinboardapi.config.web.PageRequest;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.PostController;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_OFFSET_VALUE;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_SIZE_VALUE;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class PostControllerSearchTest extends PostControllerTest {

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

        PostCategory category = PostCategory.builder()
                .categoryName("category")
                .id(0L)
                .build();

        Post post1 = Post.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .writer(user)
                .category(category)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("title2")
                .content("content2")
                .writer(user)
                .category(category)
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

        PostCategory parent = PostCategory.builder().parent(null).id(0L).categoryName("식품").build();
        PostCategory category = PostCategory.builder().parent(parent).id(2L).categoryName("과일").build();
        List<PostCategory> expectedNavigation = category.getNavigation();
        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(writer)
                .category(category)
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
                .andExpect(jsonPath("$.response.comments").isArray())
                .andExpect(jsonPath("$.response.category.id").value(category.getId()))
                .andExpect(jsonPath("$.response.category.category").value(category.getCategoryName()))
                .andExpect(jsonPath("$.response.category.navigation").isArray())
                .andExpect(jsonPath("$.response.category.navigation[0].id").value(expectedNavigation.get(0).getId()))
                .andExpect(jsonPath("$.response.category.navigation[0].category").value(expectedNavigation.get(0).getCategoryName()))
                .andExpect(jsonPath("$.response.category.navigation[1].id").value(expectedNavigation.get(1).getId()))
                .andExpect(jsonPath("$.response.category.navigation[1].category").value(expectedNavigation.get(1).getCategoryName()));
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

        PostCategory category = PostCategory.builder()
                .categoryName("category")
                .id(0L)
                .build();

        Post post1 = Post.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .writer(user)
                .category(category)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("title2")
                .content("content2")
                .writer(user)
                .category(category)
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
