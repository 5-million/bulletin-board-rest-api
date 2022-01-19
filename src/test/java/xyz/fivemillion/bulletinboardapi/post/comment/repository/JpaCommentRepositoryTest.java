package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import xyz.fivemillion.bulletinboardapi.error.EntitySaveException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired EntityManager em;
    private JPAQueryFactory query;
    private CommentRepository commentRepository;

    @BeforeEach
    void beforeEach() {
        query = new JPAQueryFactory(em);
        commentRepository = new JpaCommentRepository(em, query);
    }

    @Test
    @DisplayName("save fail: 등록되지 않은 사용자의 요청")
    void save_fail_UnknownUserRequest() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = em.find(Post.class, 2L);

        Comment comment = Comment.builder()
                .writer(writer)
                .post(post)
                .content("comment")
                .build();

        //when
        EntitySaveException thrown = assertThrows(EntitySaveException.class, () -> commentRepository.save(comment));

        //then
        assertEquals(Error.UNKNOWN_USER_OR_POST, thrown.getError());
    }

    @Test
    @DisplayName("save fail: 등록되지 않은 포스트에 대한 작성")
    void save_fail_등록되지않은포스트에작성() {
        //given
        User writer = em.find(User.class, 1L);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(writer)
                .build();

        Comment comment = Comment.builder()
                .writer(writer)
                .post(post)
                .content("comment")
                .build();

        //when
        EntitySaveException thrown = assertThrows(EntitySaveException.class, () -> commentRepository.save(comment));

        //then
        assertEquals(Error.UNKNOWN_USER_OR_POST, thrown.getError());
    }

    @Test
    @DisplayName("save success")
    void save_success() {
        //given
        User writer = em.find(User.class, 1L);
        Post post = em.find(Post.class, 2L);
        Comment comment = Comment.builder()
                .post(post)
                .writer(writer)
                .content("comment")
                .build();

        //when
        commentRepository.save(comment);

        //then
        assertTrue(writer.getComments().contains(comment));
        assertTrue(post.getComments().contains(comment));
    }
}