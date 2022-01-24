package xyz.fivemillion.bulletinboardapi.post.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.EntityManager;

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
}
