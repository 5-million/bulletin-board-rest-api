package xyz.fivemillion.bulletinboardapi.post.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

@DataJpaTest
class JpaPostCategoryRepositoryTest {

    @Autowired EntityManager em;
    private static JpaPostCategoryRepository postCategoryRepository;

    @BeforeAll
    static void beforeEach(@Autowired EntityManager em) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        postCategoryRepository = new JpaPostCategoryRepository(em, query);
    }
}