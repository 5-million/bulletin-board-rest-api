package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalPasswordException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserServiceImplRegisterTest extends UserServiceImplTest {

    @Test
    @DisplayName("register fail: pwd != confirmPwd")
    void register_fail_pwd와confirmPwd가일치하지않음() {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@email.com",
                "password",
                "pessword",
                "display name"
        );

        //when
        IllegalPasswordException exception =
                assertThrows(IllegalPasswordException.class, () -> userService.register(request));

        //then
        assertEquals(Error.CONFIRM_PASSWORD_NOT_MATCH, exception.getError());
    }

    @Test
    @DisplayName("register fail: 중복된 email")
    void register_fail_duplicateEmail() {
        //given
        String email = "abc@test.com";
        UserRegisterRequest request = new UserRegisterRequest(
                email,
                "password",
                "password",
                "displayName"
        );
        User user = User.builder()
                .email(email)
                .password("password")
                .displayName("display name")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        DuplicateException thrown = assertThrows(
                DuplicateException.class,
                () -> userService.register(request)
        );

        //then
        assertEquals(Error.EMAIL_DUPLICATE, thrown.getError());
    }

    @Test
    @DisplayName("register fail: 중복된 displayName")
    void register_fail_duplicateDisplayName() {
        //given
        String displayName = "display name";
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                displayName
        );
        User user = User.builder()
                .email("abc@test.com")
                .password("password")
                .displayName(displayName)
                .build();

        given(userRepository.findByDisplayName(displayName)).willReturn(Optional.of(user));

        //when
        DuplicateException thrown = assertThrows(
                DuplicateException.class,
                () -> userService.register(request)
        );

        //then
        assertEquals(Error.DISPLAY_NAME_DUPLICATE, thrown.getError());
    }

    @Test
    @DisplayName("register success")
    void register_success() {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@email.com",
                "password",
                "password",
                "display name"
        );

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.findByDisplayName(request.getDisplayName())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn(bCryptPasswordEncoder.encode(request.getPassword()));

        //when
        User result = userService.register(request);

        //then
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getDisplayName(), result.getDisplayName());
        assertTrue(bCryptPasswordEncoder.matches(request.getPassword(), result.getPassword()));
        assertNotNull(result.getCreateAt());
    }
}
