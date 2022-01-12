package xyz.fivemillion.bulletinboardapi.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.repository.PostRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("register fail: 등록되지 않은 사용자의 등록")
    void register_fail_등록되지않은사용자의등록() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        //when
        UnAuthorizedException thrown =
                assertThrows(UnAuthorizedException.class, () -> postService.register(writer, request));

        //then
        assertEquals(Error.UNKNOWN_USER_REGISTER, thrown.getError());
    }

    @Test
    @DisplayName("register fail: repository에서 IllegalStateException 발생(존재하지 않는 사용자)")
    void register_fail_repositoryThrowIllegalStateException() {
        //given
        User writer = User.builder()
                .id(1L)
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        doThrow(IllegalStateException.class).when(postRepository).save(any(Post.class));

        //when
        UnAuthorizedException thrown = assertThrows(
                UnAuthorizedException.class,
                () -> postService.register(writer, request)
        );

        //then
        assertEquals(Error.UNKNOWN_USER_REGISTER, thrown.getError());
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