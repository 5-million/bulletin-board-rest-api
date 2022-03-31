package xyz.fivemillion.bulletinboardapi.post.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class PostCategoryServiceImplFindTest extends PostCategoryServiceImplTest {

    private final Map<Long, PostCategory> data;

    public PostCategoryServiceImplFindTest() {
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
    @DisplayName("findOne(byId) fail: category not found")
    void findOneById_fail_categoryNotFound() {
        //given
        Long categoryId = -1L;
        given(postCategoryRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> postCategoryService.findOne(categoryId));

        //then
        assertEquals(Error.CATEGORY_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("findOne(byCategoryName) fail: category not found")
    void findOneByCategoryName_fail_categoryNotFound() {
        //given
        String categoryName = "category";
        given(postCategoryRepository.findByName(anyString())).willReturn(Optional.empty());

        //when
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> postCategoryService.findOne(categoryName));

        //then
        assertEquals(Error.CATEGORY_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("findOne(byId) success")
    void findOneById_success_haveSubcategory() {
        //given
        Long categoryId = 0L;
        given(postCategoryRepository.findById(anyLong())).willReturn(Optional.of(data.get(categoryId)));

        //when
        PostCategory result = postCategoryService.findOne(categoryId);

        //then
        PostCategory expected = data.get(categoryId);
        assertEquals(expected.getCategoryName(), result.getCategoryName());
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertEquals(expected.getParent(), result.getParent());
        assertEquals(expected.getDepth(), result.getDepth());
        assertArrayEquals(expected.getChild().toArray(new PostCategory[0]), result.getChild().toArray(new PostCategory[0]));
    }

    @Test
    @DisplayName("findOne(byCategoryName) success")
    void findOneByCategoryName_success_haveSubcategory() {
        //given
        String categoryName = "category";
        given(postCategoryRepository.findByName(anyString())).willReturn(Optional.of(data.get(0L)));

        //when
        PostCategory result = postCategoryService.findOne(categoryName);

        //then
        PostCategory expected = data.get(0L);
        assertEquals(expected.getCategoryName(), result.getCategoryName());
        assertEquals(expected.getGroupName(), result.getGroupName());
        assertEquals(expected.getParent(), result.getParent());
        assertEquals(expected.getDepth(), result.getDepth());
        assertArrayEquals(expected.getChild().toArray(new PostCategory[0]), result.getChild().toArray(new PostCategory[0]));
    }
}
