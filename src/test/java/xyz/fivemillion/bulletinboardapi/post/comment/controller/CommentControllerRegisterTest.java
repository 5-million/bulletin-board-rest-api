package xyz.fivemillion.bulletinboardapi.post.comment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.post.comment.CommentController;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.CommentRegisterRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class CommentControllerRegisterTest extends CommentControllerTest {

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
}
