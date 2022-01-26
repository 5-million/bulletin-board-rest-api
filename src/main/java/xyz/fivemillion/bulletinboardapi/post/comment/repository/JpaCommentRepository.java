package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.post.comment.QComment;
import xyz.fivemillion.bulletinboardapi.user.QUser;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.EntityManager;

import java.util.Optional;

import static xyz.fivemillion.bulletinboardapi.post.comment.QComment.comment;
import static xyz.fivemillion.bulletinboardapi.user.QUser.user;
import static xyz.fivemillion.bulletinboardapi.utils.CheckUtil.checkNotNull;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(Comment comment) {
        checkNotNull(em.find(User.class, comment.getWriter().getId()), Error.UNKNOWN_USER);
        checkNotNull(em.find(Post.class, comment.getPost().getId()), Error.UNKNOWN_POST);
        em.persist(comment);
    }

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(
                query.selectFrom(comment)
                        .leftJoin(comment.writer, user)
                        .fetchJoin()
                        .where(comment.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public void delete(Comment comment) {
        if (!em.contains(comment))
            throw new NotFoundException(Error.UNKNOWN_COMMENT);

        em.remove(comment);
    }
}
