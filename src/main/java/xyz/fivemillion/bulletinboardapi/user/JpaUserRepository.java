package xyz.fivemillion.bulletinboardapi.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static xyz.fivemillion.bulletinboardapi.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public Optional<User> findByEmail(String target) {
        return Optional.ofNullable(
                query.selectFrom(user).where(user.email.eq(target)).fetchOne()
        );
    }

    @Override
    public Optional<User> findByDisplayName(String target) {
        return Optional.ofNullable(
                query.selectFrom(user).where(user.displayName.eq(target)).fetchOne()
        );
    }
}
