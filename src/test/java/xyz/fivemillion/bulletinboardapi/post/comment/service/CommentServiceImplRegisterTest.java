package xyz.fivemillion.bulletinboardapi.post.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.BlankException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceImplRegisterTest extends CommentServiceImplTest {

    @Test
    @DisplayName("register fail: writer==null")
    void register_fail_writerIsNull() {
        //given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        String content = "comment";

        //when
        NullException thrown =
                assertThrows(NullException.class, () -> commentService.register(null, post, content));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("register fail: post==null")
    void register_fail_postIsNull() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();
        String content = "comment";

        //when
        NullException thrown =
                assertThrows(NullException.class, () -> commentService.register(writer, null, content));

        //then
        assertEquals(Error.UNKNOWN_POST, thrown.getError());
    }

    @Test
    @DisplayName("register fail: content==null")
    void register_fail_contentIsNull() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        //when
        NullException thrown =
                assertThrows(NullException.class, () -> commentService.register(writer, post, null));

        //then
        assertEquals(Error.CONTENT_IS_NULL_OR_BLANK, thrown.getError());
    }

    @Test
    @DisplayName("register fail: content==blank")
    void register_fail_contentIsBlank() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "";

        //when
        BlankException thrown =
                assertThrows(BlankException.class, () -> commentService.register(writer, post, content));

        //then
        assertEquals(Error.CONTENT_IS_NULL_OR_BLANK, thrown.getError());
    }

    @Test
    @DisplayName("register fail: 등록되지 않은 사용자의 요청")
    void register_fail_unknownUser() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "content";

        doThrow(new NullException(Error.UNKNOWN_USER)).when(commentRepository).save(any(Comment.class));

        //when
        NotFoundException thrown =
                assertThrows(NotFoundException.class, () -> commentService.register(writer, post, content));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("register fail: 등록되지 않은 포스트에 대한 요청")
    void register_fail_unknownPost() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "content";

        doThrow(new NullException(Error.UNKNOWN_POST)).when(commentRepository).save(any(Comment.class));

        //when
        NotFoundException thrown =
                assertThrows(NotFoundException.class, () -> commentService.register(writer, post, content));

        //then
        assertEquals(Error.UNKNOWN_POST, thrown.getError());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "comment";

        //when
        Comment result = commentService.register(writer, post, content);

        //then
        verify(commentRepository, times(1)).save(any(Comment.class));
        assertEquals(content, result.getContent());
        assertEquals(writer, result.getWriter());
        assertEquals(post, result.getPost());
        assertNotNull(result.getCreateAt());
    }
}
