package xyz.fivemillion.bulletinboardapi.post.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategoryController;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryHierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostCategoryControllerGetTest extends PostCategoryControllerTest {

    private final Map<Long, PostCategory> data;

    public PostCategoryControllerGetTest() {
        this.data = createData();
    }

    private Map<Long, PostCategory> createData() {
        Map<Long, PostCategory> data = new HashMap<>();

        data.put(0L, createPostCategory(0L, "식품", null));
        data.put(1L, createPostCategory(1L, "홈인테리어", null));
        data.put(2L, createPostCategory(2L, "과일", data.get(0L)));
        data.put(3L, createPostCategory(3L, "채소", data.get(0L)));
        data.put(4L, createPostCategory(4L, "봄침구샵", data.get(1L)));
        data.put(5L, createPostCategory(5L, "싱글하우스", data.get(1L)));
        data.put(6L, createPostCategory(6L, "홈데코", data.get(1L)));
        data.put(7L, createPostCategory(7L, "사과/배", data.get(2L)));
        data.put(8L, createPostCategory(8L, "키위/참다래", data.get(2L)));
        data.put(9L, createPostCategory(9L, "과일선물세트", data.get(2L)));
        data.put(10L, createPostCategory(10L, "침구커버", data.get(4L)));
        data.put(11L, createPostCategory(11L, "실내공기정화", data.get(4L)));
        data.put(12L, createPostCategory(12L, "시계", data.get(6L)));
        data.put(13L, createPostCategory(13L, "벽걸이시계", data.get(12L)));

        return data;
    }

    private PostCategory createPostCategory(Long id, String categoryName, PostCategory parent) {
        PostCategory pc = PostCategory.builder()
                .id(id)
                .categoryName(categoryName)
                .parent(parent)
                .build();

        if (parent != null)
            parent.addChild(pc);

        return pc;
    }

    @Test
    @DisplayName("getAll success")
    void getAll_success() throws Exception {
        //given
        List<PostCategory> all = new ArrayList<>();
        List<PostCategoryHierarchy> expected = new ArrayList<>();
        for (PostCategory pc : data.values())
            if (pc.getParent() == null) {
                all.add(pc);
                expected.add(new PostCategoryHierarchy(pc));
            }

        given(postCategoryService.findAll()).willReturn(all);

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(BASE_URL));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(PostCategoryController.class))
                .andExpect(handler().methodName("getAll"))
                .andExpect(jsonPath("$.response").isArray());

        result.andReturn().getResponse().getContentAsString().equals(new ObjectMapper().writeValueAsString(expected));
    }
}
