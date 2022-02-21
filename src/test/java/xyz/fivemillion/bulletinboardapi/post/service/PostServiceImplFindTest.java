package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

public class PostServiceImplFindTest extends PostServiceImplTest {

    @Test
    @DisplayName("findById fail: 존재하지 않는 포스트")
    void findById_fail_존재하지않는포스트() {
        //given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> postService.findById(1L));

        //result
        assertEquals(Error.POST_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("findById success")
    void findById_success() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .writer(writer)
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        Post result = postService.findById(1L);

        //then
        assertEquals(post, result);
    }
}
