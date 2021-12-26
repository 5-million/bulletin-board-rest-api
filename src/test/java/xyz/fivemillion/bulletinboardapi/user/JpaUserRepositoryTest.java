package xyz.fivemillion.bulletinboardapi.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired EntityManager em;
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        userRepository = new JpaUserRepository(em, jpaQueryFactory);
    }

    @Test
    @DisplayName("email 중복 등록")
    void save_fail_email중복() {
        //given
        User user1 = User.builder()
                .email("test@example.com")
                .password("password")
                .displayName("display name")
                .build();

        User user2 = User.builder()
                .email("test@example.com")
                .password("password2")
                .displayName("display name")
                .build();

        userRepository.save(user1);

        //when
        assertThrows(PersistenceException.class, () -> userRepository.save(user2));
        // org.hibernate.exception.ConstraintViolationException: could not execute statement
    }

    @Test
    @DisplayName("displayName 중복 등")
    void save_fail_displayName중복() {
        //given
        User user1 = User.builder()
                .email("abc@example.com")
                .password("password")
                .displayName("display name")
                .build();

        User user2 = User.builder()
                .email("def@example.com")
                .password("password")
                .displayName("display name")
                .build();

        userRepository.save(user1);

        //when
        assertThrows(PersistenceException.class, () -> userRepository.save(user2));
        // org.hibernate.exception.ConstraintViolationException: could not execute statement
    }

    @Test
    @DisplayName("user 등록 성공")
    void save_success() {
        //given
        User user = User.builder()
                .email("abc@example.com")
                .password("password")
                .displayName("display name")
                .build();

        //when
        userRepository.save(user);

        //then
        assertNotNull(user.getId());
        assertEquals("abc@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("display name", user.getDisplayName());
    }
}