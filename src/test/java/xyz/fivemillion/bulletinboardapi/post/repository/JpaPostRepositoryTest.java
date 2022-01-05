package xyz.fivemillion.bulletinboardapi.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaPostRepositoryTest {

    @Autowired
    private EntityManager em;
    private PostRepository postRepository;

    @BeforeEach
    void beforeEach() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        postRepository = new JpaPostRepository(em, queryFactory);
    }

    @Test
    @DisplayName("write fail: 등록되지 않은 사용자의 등록")
    void write_fail_등록되지않은사용자의등록() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .writer(writer)
                .title("title")
                .content("content")
                .build();

        //when
        assertThrows(IllegalStateException.class, () -> postRepository.save(post));
    }

    @Test
    @DisplayName("write success")
    void write_success() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName("display name")
                .build();

        em.persist(writer);

        Post post = Post.builder()
                .writer(writer)
                .title("title")
                .content("content")
                .build();

        //when
        postRepository.save(post);

        //then
        assertNotNull(post.getId());
        assertNotNull(post.getCreateAt());
        assertNotNull(post.getUpdateAt());
        assertEquals(writer.getId(), post.getWriter().getId());
        assertEquals("title", post.getTitle());
        assertEquals("content", post.getContent());
        assertEquals(0, post.getViews());
    }
}