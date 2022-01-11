package xyz.fivemillion.bulletinboardapi.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.post.Post;

import javax.persistence.EntityManager;
import java.util.List;

import static xyz.fivemillion.bulletinboardapi.post.QPost.post;
import static xyz.fivemillion.bulletinboardapi.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class JpaPostRepository implements PostRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(Post post) {
        em.persist(post);
    }

    @Override
    public List<Post> findAll(long offset, long size) {
        return query.selectFrom(post)
                .join(post.writer, user)
                .orderBy(post.createAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public List<Post> findByWriter(Long writerId) {
        return query.selectFrom(post)
                .join(post.writer, user)
                .where(user.id.eq(writerId))
                .fetch();
    }
}
