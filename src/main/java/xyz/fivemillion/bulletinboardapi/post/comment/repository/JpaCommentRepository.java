package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.error.EntitySaveException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(Comment comment) {
        try {
            em.persist(comment);
        } catch (IllegalStateException e) {
            throw new EntitySaveException(Error.UNKNOWN_USER_OR_POST);
        }
    }
}
