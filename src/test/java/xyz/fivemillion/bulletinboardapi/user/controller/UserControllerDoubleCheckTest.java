package xyz.fivemillion.bulletinboardapi.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.dto.DisplayNameCheckRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.EmailCheckRequest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDoubleCheckTest extends UserControllerTest {

    @Test
    @DisplayName("doubleCheckEmail fail: email 누락")
    void doubleCheckEmail_fail_email누락() throws Exception {
        //given
        String url = "/api/v1/user/check/email";
        EmailCheckRequest request = new EmailCheckRequest(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doubleCheckEmail fail: email 형식이 아님")
    void doubleCheckEmail_fail_email형식이아님() throws Exception {
        //given
        String url = "/api/v1/user/check/email";
        EmailCheckRequest request = new EmailCheckRequest("abc");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doubleCheckEmail success: 중복되지 않음")
    void doubleCheckEmail_success_true() throws Exception {
        //given
        String url = "/api/v1/user/check/email";
        EmailCheckRequest request = new EmailCheckRequest("abc@test.com");
        given(userService.findByEmail(anyString())).willReturn(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("true"));
    }

    @Test
    @DisplayName("doubleCheckEmail success: 중복됨")
    void doubleCheckEmail_success_false() throws Exception {
        //given
        String url = "/api/v1/user/check/email";
        EmailCheckRequest request = new EmailCheckRequest("abc@test.com");
        given(userService.findByEmail(anyString())).willReturn(
                User.builder()
                        .email("abc@test.com")
                        .build()
        );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("false"));
    }

    @Test
    @DisplayName("doubleCheckDisplayName fail: displayName 6자 미만")
    void doubleCheckDisplayName_fail_displayName6자미만() throws Exception {
        //given
        String url = "/api/v1/user/check/displayname";
        DisplayNameCheckRequest request = new DisplayNameCheckRequest("name");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doubleCheckDisplayName fail: displayName 20자 초과")
    void doubleCheckDisplayName_fail_displayName20자초과() throws Exception {
        //given
        String url = "/api/v1/user/check/displayname";
        DisplayNameCheckRequest request = new DisplayNameCheckRequest("namenamenamenamenamename");

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doubleCheckDisplayName fail: displayName 누락")
    void doubleCheckDisplayName_fail_displayName누락() throws Exception {
        //given
        String url = "/api/v1/user/check/displayname";
        DisplayNameCheckRequest request = new DisplayNameCheckRequest(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doubleCheckDisplayName success: 중복되지 않음")
    void doubleCheckDisplayName_success_중복되지않음() throws Exception {
        //given
        String url = "/api/v1/user/check/displayname";
        DisplayNameCheckRequest request = new DisplayNameCheckRequest("display name");
        given(userService.findByDisplayName(anyString())).willReturn(null);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("true"));
    }

    @Test
    @DisplayName("doubleCheckDisplayName success: 중복됨")
    void doubleCheckDisplayName_success_중복() throws Exception {
        //given
        String url = "/api/v1/user/check/displayname";
        DisplayNameCheckRequest request = new DisplayNameCheckRequest("display name");
        given(userService.findByDisplayName(anyString())).willReturn(
                User.builder()
                        .displayName(request.getDisplayName())
                        .build()
        );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("false"));
    }
}
