package xyz.fivemillion.bulletinboardapi.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalPasswordException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserController;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class UserControllerRegisterTest extends UserControllerTest {

    private ResultActions performRegister(UserRegisterRequest request) throws Exception {
        String url = "/api/v1/user/register";
        return mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("register fail: email형식이 아닌 경우")
    void register_fail_email형식이아님() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc",
                "password",
                "password",
                "display name"
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: email이 누락된 경우")
    void register_fail_email누락() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                null,
                "password",
                "password",
                "display name"
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: displayName 6자 미만인 경우")
    void register_fail_displayName6자미만() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                "name"
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: displayName 20자 초과인 경우")
    void register_fail_displayName20자초과() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                "namenamenamenamenamename"
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: displayName 누락된 경우")
    void register_fail_displayName누락() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                null
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: email 중복")
    void register_fail_email중복() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                "display name"
        );

        given(userService.register(any()))
                .willThrow(new DuplicateException(Error.EMAIL_DUPLICATE));

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/register")
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.error.message").value(Error.EMAIL_DUPLICATE.getMessage()))
                .andExpect(status().isConflict());

        assertEquals(DuplicateException.class, getException(result).getClass());
        assertEquals(Error.EMAIL_DUPLICATE, getError(result));
    }

    @Test
    @DisplayName("register fail: displayName 중복")
    void register_fail_displayName중복() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                "display name"
        );

        given(userService.register(any(UserRegisterRequest.class)))
                .willThrow(new DuplicateException(Error.DISPLAY_NAME_DUPLICATE));

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.error.message").value(Error.DISPLAY_NAME_DUPLICATE.getMessage()))
                .andExpect(status().isConflict());

        assertEquals(DuplicateException.class, getException(result).getClass());
        assertEquals(Error.DISPLAY_NAME_DUPLICATE, getError(result));
    }

    @Test
    @DisplayName("register fail: pwd != confirmPwd")
    void register_fail_pwd와confirmPwd일치하지않음() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "pessword",
                "display name"
        );

        given(userService.register(any(UserRegisterRequest.class)))
                .willThrow(new IllegalPasswordException(Error.CONFIRM_PASSWORD_NOT_MATCH));

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("register"))
                .andExpect(jsonPath("$.error.message").value(Error.CONFIRM_PASSWORD_NOT_MATCH.getMessage()))
                .andExpect(status().isBadRequest());

        assertEquals(IllegalPasswordException.class, getException(result).getClass());
        assertEquals(Error.CONFIRM_PASSWORD_NOT_MATCH, getError(result));
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //given
        String email = "abc@test.com";
        String password = "password";
        String displayName = "display name";
        UserRegisterRequest request = new UserRegisterRequest(
                email,
                password,
                password,
                displayName
        );

        given(userService.register(any(UserRegisterRequest.class))).willReturn(
                User.builder()
                        .email(email)
                        .displayName(displayName)
                        .password(password)
                        .build()
        );

        //when
        ResultActions result = performRegister(request);

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.email").value(email))
                .andExpect(jsonPath("$.response.displayName").value(displayName))
                .andExpect(jsonPath("$.response.createAt").exists());
    }
}
