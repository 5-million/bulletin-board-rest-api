package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PostServiceImplDeleteTest extends PostServiceImplTest {

    @Test
    @DisplayName("delete fail: 존재하지 않는 포스트")
    void delete_fail_존재하지않는포스트() {
        //given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> postService.delete(any(User.class), 1L));

        //then
        assertEquals(Error.POST_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("delete fail: Forbidden(포스트 작성자가 아님)")
    void delete_fail_forbidden() {
        //given
        User requester = User.builder()
                .email("abcd@test.com")
                .displayName("display name1")
                .build();

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

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> postService.delete(requester, 1L));

        //then
        assertEquals(Error.NOT_POST_OWNER, thrown.getError());
    }

    @Test
    @DisplayName("delete success")
    void delete_success() {
        //given
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

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        postService.delete(writer, 1L);

        //then
        verify(postRepository, times(1)).delete(post);
    }
}
