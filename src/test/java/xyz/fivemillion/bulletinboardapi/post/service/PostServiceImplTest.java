package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.repository.PostRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

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