package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.LoginException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class UserServiceImplLoginTest extends UserServiceImplTest {

    @Test
    @DisplayName("login fail: 사용자를 찾을 수 없음")
    void login_fail_사용자를찾을수없음() {
        //given
        LoginRequest request = new LoginRequest(
                "abc@test.com",
                "password"
        );

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when
        LoginException thrown = assertThrows(LoginException.class, () -> userService.login(request));

        //then
        assertEquals(Error.USER_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("login fail: 암호가 맞지 않음")
    void login_fail_암호가틀림() {
        //given
        LoginRequest request = new LoginRequest(
                "abc@test.com",
                "pessword"
        );

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(
                User.builder()
                        .email("abc@test.com")
                        .password(bCryptPasswordEncoder.encode("password"))
                        .build()
        ));

        //when
        LoginException thrown = assertThrows(
                LoginException.class,
                () -> userService.login(request)
        );

        //then
        assertEquals(Error.EMAIL_AND_PASSWORD_NOT_MATCH, thrown.getError());
    }

    @Test
    @DisplayName("login success")
    void login_success() {
        //given
        LoginRequest request = new LoginRequest(
                "abc@test.com",
                "password"
        );

        User user = User.builder()
                .email(request.getEmail())
                .displayName("display name")
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        //when
        User result = userService.login(request);

        //then
        assertEquals(request.getEmail(), result.getEmail());
        assertTrue(bCryptPasswordEncoder.matches(request.getPassword(), result.getPassword()));
        assertNotNull(result.getDisplayName());
    }
}
