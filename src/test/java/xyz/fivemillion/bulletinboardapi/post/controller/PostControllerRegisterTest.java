package xyz.fivemillion.bulletinboardapi.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class PostControllerRegisterTest extends PostControllerTest {

    @Test
    @DisplayName("register fail: 인증되지 않은 사용자")
    void register_fail_인증되지않은사용자() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("register fail: 존재하지 않는 사용자")
    void register_fail_존재하지않는사용자() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result
                .andDo(print())
                .andExpect(status().isUnauthorized());

        assertEquals(NotFoundException.class, getException(result).getClass());
        assertEquals(Error.UNKNOWN_USER, getError(result));
    }

    @Test
    @DisplayName("register fail: title is blank")
    void register_fail_titleIsBlank() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("", "content");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: 제목 길이제한 초과")
    void register_fail_제목길이제한초과() throws Exception {
        //given
        String title = "titletitletitletitletitletitletitletitleti" +
                "tletitletitletitletitletitletitletitletitletitletitletitlet" +
                "itletitletitletitletitletitletitletitletitletitle";

        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest(title, "content");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: content is blank")
    void register_fail_contentIsBlank() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //given
        User user = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        String token = tokenUtil.generateJwtToken(user);
        PostRegisterRequest request = new PostRegisterRequest("title", "content");

        given(userService.findByEmail(anyString())).willReturn(user);
        given(postService.register(any(User.class), any(PostRegisterRequest.class))).willReturn(
                Post.builder()
                        .title(request.getTitle())
                        .content(request.getTitle())
                        .writer(user)
                        .build()
        );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .header(AUTH_HEADER_NAME, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
        );

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("title"))
                .andExpect(jsonPath("$.response.writer").value("display name"))
                .andExpect(jsonPath("$.response.views").value("0"))
                .andExpect(jsonPath("$.response.commentCount").value("0"))
                .andExpect(jsonPath("$.response.createAt").exists())
                .andExpect(jsonPath("$.response.updateAt").exists());
    }
}
