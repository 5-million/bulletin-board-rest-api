package xyz.fivemillion.bulletinboardapi.post.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryRegisterRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PostCategoryServiceImplRegisterTest extends PostCategoryServiceImplTest {

    @Test
    @DisplayName("register fail: requestDto == null")
    void register_fail_requestDtoIsNull() {
        //given
        PostCategoryRegisterRequest pcrr = null;

        //when
        NullException thrown = assertThrows(NullException.class, () -> postCategoryService.register(pcrr));

        //then
        assertEquals(Error.REQUEST_DTO_IS_NULL, thrown.getError());
    }

    @Test
    @DisplayName("register fail: unregistered parent")
    void register_fail_unregisteredParent() {
        //given
        Long parentId = 0L;
        PostCategoryRegisterRequest pcrr = new PostCategoryRegisterRequest("newCategory", parentId);

        given(postCategoryRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> postCategoryService.register(pcrr));

        //then
        assertEquals(Error.UNKNOWN_CATEGORY, thrown.getError());
    }

    @Test
    @DisplayName("register fail: already exist category")
    void register_fail_alreadyExistCategory() {
        //given
        PostCategory category = PostCategory.builder().categoryName("newCategory").build();
        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest("newCategory", 0L);

        given(postCategoryRepository.findByName(anyString())).willReturn(Optional.of(category));

        //when
        DuplicateException thrown = assertThrows(DuplicateException.class, () -> postCategoryService.register(request));

        //then
        assertEquals(Error.CATEGORY_DUPLICATE, thrown.getError());
    }

    @Test
    @DisplayName("register success: no parent")
    void register_success_noParent() {
        //given
        String categoryName = "newCategory";
        PostCategoryRegisterRequest pcrr = new PostCategoryRegisterRequest(categoryName, null);

        //when
        PostCategory result = postCategoryService.register(pcrr);

        //then
        verify(postCategoryRepository, times(1)).save(any(PostCategory.class));
        assertEquals(categoryName, result.getCategoryName());
        assertNull(result.getParent());
        assertEquals(0, result.getDepth());
        assertEquals(categoryName, result.getGroupName());
    }

    @Test
    @DisplayName("register success: exist parent")
    void register_success_existParent() {
        //given
        String categoryName = "newCategory";
        Long parentId = 1L;
        PostCategoryRegisterRequest pcrr = new PostCategoryRegisterRequest(categoryName, parentId);

        PostCategory parent = PostCategory.builder() // depth = 0;
                .categoryName("parent")
                .build();

        given(postCategoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(parent));

        //when
        PostCategory result = postCategoryService.register(pcrr);

        //then
        verify(postCategoryRepository, times(1)).save(any(PostCategory.class));
        assertEquals(categoryName, result.getCategoryName());
        assertNotNull(result.getParent());
        assertEquals(parent.getCategoryName(), result.getParent().getCategoryName());
        assertEquals(parent.getDepth() + 1, result.getDepth());
        assertEquals(parent.getGroupName(), result.getGroupName());
    }
}
