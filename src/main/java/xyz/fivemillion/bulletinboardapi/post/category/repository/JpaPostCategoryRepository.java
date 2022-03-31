package xyz.fivemillion.bulletinboardapi.post.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.QPostCategory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static xyz.fivemillion.bulletinboardapi.post.category.QPostCategory.*;

@Repository
@RequiredArgsConstructor
public class JpaPostCategoryRepository implements PostCategoryRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(PostCategory postCategory) {
        em.persist(postCategory);
    }

    @Override
    public Optional<PostCategory> findById(Long id) {
        return Optional.ofNullable(em.find(PostCategory.class, id));
    }

    @Override
    public Optional<PostCategory> findByName(String name) {
        return Optional.ofNullable(
                query.selectFrom(postCategory)
                        .where(postCategory.categoryName.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public List<PostCategory> findAll() {
        return query.selectFrom(postCategory)
                .where(postCategory.depth.eq(0))
                .fetch();
    }
}
