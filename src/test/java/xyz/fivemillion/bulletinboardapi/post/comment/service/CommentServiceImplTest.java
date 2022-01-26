package xyz.fivemillion.bulletinboardapi.post.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.error.*;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.post.comment.repository.CommentRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    CommentServiceImpl commentService;

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

    @Test
    @DisplayName("delete fail: requester == null")
    void delete_fail_unknownUser() {
        //when
        NullException thrown = assertThrows(NullException.class, () -> commentService.delete(null, 1));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("delete fail: 댓글의 작성자가 아님")
    void delete_fail_notCommentOwner() {
        //given
        User requester = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        User owner = User.builder()
                .email("def@test.com")
                .displayName("comment owner")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        Comment comment = Comment.builder()
                .writer(owner)
                .post(post)
                .content("comment")
                .build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        //when
        NotOwnerException thrown = assertThrows(NotOwnerException.class, () -> commentService.delete(requester, 1));

        //then
        assertEquals(Error.NOT_COMMENT_OWNER, thrown.getError());
    }

    @Test
    @DisplayName("delete success")
    void delete_success() {
        //given
        User owner = User.builder()
                .email("def@test.com")
                .displayName("comment owner")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        Comment comment = Comment.builder()
                .writer(owner)
                .post(post)
                .content("comment")
                .build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        //when
        commentService.delete(owner, 1);

        //then
        verify(commentRepository, times(1)).delete(comment);
    }
}