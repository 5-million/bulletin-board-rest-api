package xyz.fivemillion.bulletinboardapi.post.category.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class PostCategoryServiceImplEtcTest extends PostCategoryServiceImplTest {

    @Test
    @DisplayName("isExist fail: categoryName == null")
    void isExist_fail_categoryNameIsNull() {
        //given

        //when
        NullException thrown = assertThrows(NullException.class, () -> postCategoryService.isExist(null));

        //then
        assertEquals(Error.ARGUMENT_IS_NULL, thrown.getError());
    }

    @Test
    @DisplayName("isExist success: true")
    void isExist_success_true() {
        //given
        PostCategory old = PostCategory.builder().categoryName("category").build();
        given(postCategoryRepository.findByName(anyString())).willReturn(Optional.of(old));

        //when
        boolean result = postCategoryService.isExist("category");

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("isExist success: false")
    void isExist_success_false() {
        //given
        given(postCategoryRepository.findByName(anyString())).willThrow(NotFoundException.class);

        //when
        boolean result = postCategoryService.isExist("category");

        //then
        assertFalse(result);
    }
}
