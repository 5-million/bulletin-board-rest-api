package xyz.fivemillion.bulletinboardapi.post.comment;

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
import org.springframework.web.bind.MethodArgumentNotValidException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.CommentRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.comment.service.CommentService;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean CommentService commentService;
    @MockBean UserService userService;
    @MockBean PostService postService;
    @Autowired JwtTokenUtil tokenUtil;
    @Autowired MockMvc mvc;

    private final Gson gson = new Gson();
    private final String TOKEN_HEADER_NAME = "X-FM-AUTH";
    private final String TOKEN_PREFIX = "Bearer ";
    private final User requester = User.builder()
            .email("abc@test.com")
            .displayName("display name")
            .build();

    @Test
    @DisplayName("register fail: 인증되지 않은 요청")
    void register_fail_unauthorizedRequest() throws Exception {
        //given
        String url = "/api/v1/comment";

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.post(url)).andDo(print());

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("register fail: 존재하지 않는 사용자")
    void register_fail_unknownUser() throws Exception {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(1L, "comment");
        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = performRegister(gson.toJson(request));

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(status().isUnauthorized());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_USER, getError(result));
    }

    private ResultActions performRegister(String jsonBody) throws Exception {
        String url = "/api/v1/comment";

        return mvc.perform(MockMvcRequestBuilders
                .post(url)
                .header(TOKEN_HEADER_NAME, TOKEN_PREFIX + createJwtToken())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private String createJwtToken() {
        return tokenUtil.generateJwtToken(requester);
    }

    @Test
    @DisplayName("register fail: 존재하지 않는 포스트")
    void register_fail_postNotFound() throws Exception {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(1L, "comment");
        given(userService.findByEmail(anyString())).willReturn(requester);
        given(postService.findById(anyLong())).willThrow(new NotFoundException(Error.POST_NOT_FOUND));

        //when
        ResultActions result = performRegister(gson.toJson(request));

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(status().isBadRequest());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_POST, getError(result));
    }

    @Test
    @DisplayName("register fail: 내용 누락")
    void register_fail_contentIsNull() throws Exception {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("postId", "1");

        //when
        ResultActions result = performRegister(gson.toJson(body));

        //then
        result.andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class, getException(result).getClass());
    }

    @Test
    @DisplayName("register fail: 내용 공백")
    void register_fail_contentIsBlank() throws Exception {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("postId", "1");
        body.put("content", "");

        //when
        ResultActions result = performRegister(gson.toJson(body));

        //then
        result.andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class, getException(result).getClass());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //then
        Post targetPost = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();

        CommentRegisterRequest request = new CommentRegisterRequest(1L, "comment");
        Comment comment = Comment.builder()
                .id(1L)
                .writer(requester)
                .post(targetPost)
                .content(request.getContent())
                .build();

        given(userService.findByEmail(anyString())).willReturn(requester);
        given(postService.findById(anyLong())).willReturn(targetPost);
        given(commentService.register(requester, targetPost, request.getContent())).willReturn(comment);

        //when
        ResultActions result = performRegister(gson.toJson(request));

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.response.writer").value(requester.getDisplayName()))
                .andExpect(jsonPath("$.response.postId").value(targetPost.getId()))
                .andExpect(jsonPath("$.response.content").value(request.getContent()))
                .andExpect(jsonPath("$.response.createAt").exists());
    }

    @Test
    @DisplayName("delete fail: 인증되지 않은 사용자의 요청")
    void delete_fail_unauthorized() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete fail: 등록되지 않은 사용자의 요청")
    void delete_fail_unknownUser() throws Exception {
        //given
        User requester = User.builder()
                .email("abc@test.com")
                .displayName("requester")
                .build();

        String token = tokenUtil.generateJwtToken(requester);

        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/comment/1")
                        .header(TOKEN_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete fail: 존재하지 않는 댓글에 대한 요청")
    void delete_fail_unknownComment() throws Exception {
        //given
        User requester = User.builder()
                .email("abc@test.com")
                .displayName("requester")
                .build();

        String token = tokenUtil.generateJwtToken(requester);

        given(userService.findByEmail(anyString())).willReturn(requester);
        doThrow(new NotFoundException(Error.UNKNOWN_COMMENT)).when(commentService).delete(any(User.class), anyLong());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/comment/1")
                        .header(TOKEN_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isBadRequest());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_COMMENT, getError(result));
    }

    @Test
    @DisplayName("delete fail: 댓글의 작성자가 아닌 사용자의 요청")
    void delete_fail_notCommentOwner() throws Exception {
        //given
        User requester = User.builder()
                .email("abc@test.com")
                .displayName("requester")
                .build();

        String token = tokenUtil.generateJwtToken(requester);

        given(userService.findByEmail(anyString())).willReturn(requester);
        doThrow(new NotOwnerException(Error.NOT_COMMENT_OWNER)).when(commentService).delete(any(User.class), anyLong());

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/comment/1")
                        .header(TOKEN_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden());

        assertEquals(NotOwnerException.class, getException(result).getClass());
        assertEquals(Error.NOT_COMMENT_OWNER, getError(result));
    }

    @Test
    @DisplayName("delete success")
    void delete_success() throws Exception {
        //given
        User requester = User.builder()
                .email("abc@test.com")
                .displayName("requester")
                .build();

        String token = tokenUtil.generateJwtToken(requester);

        given(userService.findByEmail(anyString())).willReturn(requester);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/comment/1")
                        .header(TOKEN_HEADER_NAME, TOKEN_PREFIX + token)
        ).andDo(print());

        //then
        result
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).delete(requester, 1);
    }
}