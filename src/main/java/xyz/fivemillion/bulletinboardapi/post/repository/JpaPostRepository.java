package xyz.fivemillion.bulletinboardapi.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.post.Post;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static xyz.fivemillion.bulletinboardapi.post.QPost.post;
import static xyz.fivemillion.bulletinboardapi.post.comment.QComment.comment;
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
    public Optional<Post> findById(long id) {
        return Optional.ofNullable(
                query.selectFrom(post)
                        .leftJoin(post.comments, comment)
                        .fetchJoin()
                        .leftJoin(post.writer, user)
                        .fetchJoin()
                        .where(post.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<Post> findAll(long offset, long size) {
        return query.selectFrom(post)
                .leftJoin(post.writer, user)
                .fetchJoin()
                .orderBy(post.createAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }


    @Override
    public List<Post> findByQuery(String q, long offset, long size) {
        return query.selectFrom(post)
                .leftJoin(post.writer, user)
                .fetchJoin()
                .where(post.title.contains(q).or(post.content.contains(q)))
                .orderBy(post.createAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public void delete(Post post) {
        em.remove(post);
    }
}
