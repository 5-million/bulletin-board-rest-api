package xyz.fivemillion.bulletinboardapi.post.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategoryController;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class PostCategoryControllerRegisterTest extends PostCategoryControllerTest {


    private static final String AUTH_HEADER_KEY = "X-FM-AUTH";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final User requester = User.builder()
            .email("abc@test.com")
            .displayName("display name")
            .build();
    private static final ObjectMapper om = new ObjectMapper();
    private String token;

    public PostCategoryControllerRegisterTest(@Autowired JwtTokenUtil jwtTokenUtil) {
        this.token = TOKEN_PREFIX + jwtTokenUtil.generateJwtToken(requester);
    }

    private ResultActions performRegister(PostCategoryRegisterRequest request) throws Exception {
        return mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_KEY, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
        ).andDo(print());
    }

    @Test
    @DisplayName("register fail: parent not found")
    void register_fail_parentNotFound() throws Exception {
        //given
        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest("newCategory", 0L);
        given(userService.findByEmail(anyString())).willReturn(requester);
        given(postCategoryService.register(any(PostCategoryRegisterRequest.class)))
                .willThrow(new NotFoundException(Error.UNKNOWN_CATEGORY));

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(PostCategoryController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.error.message").value(Error.UNKNOWN_CATEGORY.getMessage()));

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_CATEGORY, getError(result));
    }

    @Test
    @DisplayName("register fail: unauthorized")
    void register_fail_unauthorized() throws Exception {
        //given
        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest("newCategory", 0L);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
        ).andDo(print());

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("register fail: unknown user request")
    void register_fail_unknownUserRequest() throws Exception {
        //given
        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest("newCategory", 0L);
        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(PostCategoryController.class))
                .andExpect(handler().methodName("register"));

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_USER, getError(result));
    }

    @Test
    @DisplayName("register fail: already exist category")
    void register_fail_alreadyExistCategory() throws Exception {
        //given
        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest("newCategory", 0L);
        given(userService.findByEmail(anyString())).willReturn(requester);
        given(postCategoryService.register(any(PostCategoryRegisterRequest.class)))
                .willThrow(new DuplicateException(Error.CATEGORY_DUPLICATE));

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(status().isConflict())
                .andExpect(handler().handlerType(PostCategoryController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.error.message").value(Error.CATEGORY_DUPLICATE.getMessage()));

        assertEquals(DuplicateException.class, getException(result).getClass());
        assertEquals(Error.CATEGORY_DUPLICATE, getError(result));
    }

    @Test
    @DisplayName("register fail: parentId is not Long type")
    void register_fail_parentIdIsNotLongType() throws Exception {
        //given
        Map<String, String> data = new HashMap<>();
        data.put("categoryName", "newCategory");
        data.put("parentId", "id");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_KEY, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data))
        ).andDo(print());

        //then
        result.andExpect(status().isBadRequest());

        assertEquals(HttpMessageNotReadableException.class, getException(result).getClass());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //then
        PostCategory parent = PostCategory.builder()
                .id(0L)
                .categoryName("parent")
                .build();

        Long categoryId = 1L;
        String categoryName = "newCategory";
        PostCategory newCategory = PostCategory.builder()
                .id(categoryId)
                .categoryName(categoryName)
                .parent(parent)
                .build();

        PostCategoryRegisterRequest request = new PostCategoryRegisterRequest(categoryName, parent.getId());
        given(userService.findByEmail(anyString())).willReturn(requester);
        given(postCategoryService.register(any(PostCategoryRegisterRequest.class))).willReturn(newCategory);

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(PostCategoryController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.response.id").value(newCategory.getId()))
                .andExpect(jsonPath("$.response.category").value(newCategory.getCategoryName()))
                .andExpect(jsonPath("$.response.depth").value(newCategory.getDepth()))
                .andExpect(jsonPath("$.response.group").value(newCategory.getGroupName()))
                .andExpect(jsonPath("$.response.parent").isMap())
                .andExpect(jsonPath("$.response.parent.id").value(parent.getId()))
                .andExpect(jsonPath("$.response.parent.category").value(parent.getCategoryName()))
                .andExpect(jsonPath("$.response.parent.depth").value(parent.getDepth()))
                .andExpect(jsonPath("$.response.subCategory").isArray());
    }
}
