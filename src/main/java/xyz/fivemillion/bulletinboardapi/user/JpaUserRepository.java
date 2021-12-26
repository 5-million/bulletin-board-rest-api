package xyz.fivemillion.bulletinboardapi.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(User user) {
        em.persist(user);
    }
}
