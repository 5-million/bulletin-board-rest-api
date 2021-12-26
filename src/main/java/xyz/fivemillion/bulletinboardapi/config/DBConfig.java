package xyz.fivemillion.bulletinboardapi.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.fivemillion.bulletinboardapi.user.JpaUserRepository;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class DBConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public UserRepository userRepository() {
        return new JpaUserRepository(em, jpaQueryFactory());
    }
}
