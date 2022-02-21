package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceImplRegisterTest extends PostServiceImplTest {

    @Test
    @DisplayName("register fail: writer == null")
    void register_fail_writerIsNull() {
        //given
        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        //when
        NullException thrown = assertThrows(NullException.class, () -> postService.register(null, request));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("register fail: request == null")
    void register_fail_requestIsNull() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        //when
        NullException thrown = assertThrows(NullException.class, () -> postService.register(writer, null));

        //then
        assertEquals(Error.REQUEST_DTO_IS_NULL, thrown.getError());
    }

    @Test
    @DisplayName("register fail: 등록되지 않은 사용자의 요청")
    void register_fail_등록되지않은사용자의등록() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        PostRegisterRequest request = new PostRegisterRequest("title", "content");
        doThrow(new NullException(Error.UNKNOWN_USER)).when(postRepository).save(any(Post.class));

        //when
        NotFoundException thrown =
                assertThrows(NotFoundException.class, () -> postService.register(writer, request));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("register success")
    void register_success() {
        //given
        User writer = User.builder()
                .id(1L)
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        //when
        Post result = postService.register(writer, request);

        //then
        verify(postRepository, times(1)).save(any(Post.class));

        assertNotNull(result.getCreateAt());
        assertNotNull(result.getUpdateAt());
        assertEquals(0, result.getViews());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(writer.getId(), result.getWriter().getId());
    }
}
