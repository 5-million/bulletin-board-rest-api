package xyz.fivemillion.bulletinboardapi.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.post.Post;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JpaPostRepository implements PostRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(Post post) {
        em.persist(post);
    }
}
