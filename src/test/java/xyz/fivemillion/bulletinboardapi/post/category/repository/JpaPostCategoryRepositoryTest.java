package xyz.fivemillion.bulletinboardapi.post.category.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryDto;

import javax.persistence.EntityManager;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaPostCategoryRepositoryTest {

    @Autowired EntityManager em;
    private static JpaPostCategoryRepository postCategoryRepository;

    @BeforeAll
    static void beforeEach(@Autowired EntityManager em) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        postCategoryRepository = new JpaPostCategoryRepository(em, query);
    }
}