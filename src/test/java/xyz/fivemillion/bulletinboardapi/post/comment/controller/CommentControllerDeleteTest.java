package xyz.fivemillion.bulletinboardapi.post.comment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.post.comment.CommentController;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class CommentControllerDeleteTest extends CommentControllerTest {

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
