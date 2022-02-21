package xyz.fivemillion.bulletinboardapi.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.LoginException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthentication;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthenticationToken;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserController;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getError;
import static xyz.fivemillion.bulletinboardapi.utils.ResultActionsUtil.getException;

public class UserControllerLoginTest extends UserControllerTest {

    private ResultActions performLogin(LoginRequest request) throws Exception {
        String url = "/api/v1/user/login";
        return mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("login fail: email 형식이 아님")
    void login_fail_email형식이아님() throws Exception {
        //given
        LoginRequest request = new LoginRequest("abc", "password");

        //when
        ResultActions result = performLogin(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login fail: email 누락")
    void login_fail_email누락() throws Exception {
        //given
        LoginRequest request = new LoginRequest(null, "password");

        //when
        ResultActions result = performLogin(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login fail: password 8자 미만")
    void login_fail_pwd8자미만() throws Exception {
        //given
        LoginRequest request = new LoginRequest("abc@test.com", "pwd");

        //when
        ResultActions result = performLogin(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login fail: password 누락")
    void login_fail_pwd누락() throws Exception {
        //given
        LoginRequest request = new LoginRequest("abc@test.com", null);

        //when
        ResultActions result = performLogin(request);

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login fail: email에 해당하는 사용자 없음")
    void login_fail_userNotFound() throws Exception {
        //given
        LoginRequest request = new LoginRequest("abc@test.com", "password");
        given(authenticationManager.authenticate(any(JwtAuthenticationToken.class))).willThrow(
                new LoginException(Error.USER_NOT_FOUND)
        );

        //when
        ResultActions result = performLogin(request);

        //then
        result
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(Error.USER_NOT_FOUND.getMessage()));

        assertEquals(LoginException.class, getException(result).getClass());
        assertEquals(Error.USER_NOT_FOUND, getError(result));
    }

    @Test
    @DisplayName("login fail: password가 일치하지 않음")
    void login_fail_incorrectPwd() throws Exception {
        //given
        LoginRequest request = new LoginRequest("abc@test.com", "password");
        given(authenticationManager.authenticate(any(JwtAuthenticationToken.class))).willThrow(
                new LoginException(Error.EMAIL_AND_PASSWORD_NOT_MATCH)
        );

        //when
        ResultActions result = performLogin(request);

        //then
        result
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error.message").value(Error.EMAIL_AND_PASSWORD_NOT_MATCH.getMessage())
                );

        assertEquals(LoginException.class, getException(result).getClass());
        assertEquals(Error.EMAIL_AND_PASSWORD_NOT_MATCH, getError(result));
    }

    @Test
    @DisplayName("login success")
    void login_success() throws Exception {
        //given
        String email = "abc@test.com";
        String displayName = "display name";

        LoginRequest request = new LoginRequest(email, "password");

        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                new JwtAuthentication(user.getEmail(), user.getDisplayName()),
                null,
                new ArrayList<>()
        );
        authentication.setDetails(user);

        given(authenticationManager.authenticate(any(JwtAuthenticationToken.class))).willReturn(authentication);

        //when
        ResultActions result = performLogin(request);

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isString())
                .andExpect(jsonPath("$.response").exists());

        String token = (String) gson.fromJson(result.andReturn().getResponse().getContentAsString(), ApiUtil.ApiResult.class).getResponse();
        assertEquals(user.getEmail(), jwtTokenUtil.getEmailFromToken(token));
        assertEquals(user.getDisplayName(), jwtTokenUtil.getDisplayNameFromToken(token));
    }
}
