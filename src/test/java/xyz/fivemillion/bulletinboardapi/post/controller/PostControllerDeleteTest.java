package xyz.fivemillion.bulletinboardapi.post.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.PostController;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class PostControllerDeleteTest extends PostControllerTest {

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
}
