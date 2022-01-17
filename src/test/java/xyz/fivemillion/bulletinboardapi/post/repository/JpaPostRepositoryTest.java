package xyz.fivemillion.bulletinboardapi.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_OFFSET_VALUE;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_SIZE_VALUE;

@DataJpaTest
class JpaPostRepositoryTest {

    @Autowired
    private EntityManager em;
    private static PostRepository postRepository;

    @BeforeAll
    static void beforeAll(@Autowired EntityManager entityManager) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        postRepository = new JpaPostRepository(entityManager, query);
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
        Post post = Post.builder()
                .writer(em.find(User.class, 1L))
                .title("title")
                .content("content")
                .build();

        //when
        postRepository.save(post);

        //then
        assertNotNull(post.getId());
        assertNotNull(post.getCreateAt());
        assertNotNull(post.getUpdateAt());
        assertEquals(1L, post.getWriter().getId());
        assertEquals("title", post.getTitle());
        assertEquals("content", post.getContent());
        assertEquals(0, post.getViews());
    }

    @Test
    @DisplayName("findAll (offset=0, size=100)")
    void findAll_1() {
        //given
        long offset = 0;
        long size = 100;

        //when
        List<Post> result = postRepository.findAll(offset, size);

        //then
        assertEquals(100, result.size());
        assertEquals(101, result.get(0).getId());
        assertEquals(2, result.get(99).getId());
    }

    @Test
    @DisplayName("findAll (offset=2, size=1)")
    void findAll_2() {
        //given
        long offset = 2;
        long size = 1;

        //when
        List<Post> result = postRepository.findAll(offset, size);

        //then
        assertEquals(1, result.size());
        assertEquals(99, result.get(0).getId());
    }

    @Test
    @DisplayName("findAll (offset=5, size=5)")
    void findAll_3() {
        //given
        long offset = 5;
        long size = 5;

        //when
        List<Post> result = postRepository.findAll(offset, size);

        //then
        assertEquals(5, result.size());
        assertEquals(96, result.get(0).getId());
        assertEquals(92, result.get(result.size() - 1).getId());
    }

    @Test
    @DisplayName("findByWriter: empty")
    void findByWriter_empty() {
        //given
        Long writerId = 5L;

        //when
        List<Post> result = postRepository.findByWriter(writerId);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByWriter: present")
    void findByWriter_present() {
        //given
        Long writerId = 3L;

        //when
        List<Post> result = postRepository.findByWriter(writerId);

        //then
        assertFalse(result.isEmpty());
        for (Post res : result) {
            assertEquals(res.getWriter().getId(), writerId);
        }
    }

    @Test
    @DisplayName("findByQuery")
    void findByQuery() {
        //given
        String query = "ab";

        //when
        List<Post> result = postRepository.findByQuery(query, DEFAULT_OFFSET_VALUE, DEFAULT_SIZE_VALUE);

        //then
        for (Post post : result) {
            assertTrue(post.getTitle().contains(query) || post.getContent().contains(query));
        }
    }
}