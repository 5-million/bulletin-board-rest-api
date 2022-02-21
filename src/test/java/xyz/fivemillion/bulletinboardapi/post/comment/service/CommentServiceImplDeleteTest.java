package xyz.fivemillion.bulletinboardapi.post.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommentServiceImplDeleteTest extends CommentServiceImplTest {

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
