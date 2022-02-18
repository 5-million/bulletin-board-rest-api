package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalPasswordException;
import xyz.fivemillion.bulletinboardapi.error.LoginException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserServiceImpl userService;

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

    @Test
    @DisplayName("findByEmail: 데이터가 있는 경우")
    void findByEmail_success_existData() {
        //given
        String target = "abc@example.com";
        User user = User.builder()
                .email(target)
                .password("password")
                .displayName("display name")
                .build();

        given(userRepository.findByEmail(target)).willReturn(Optional.ofNullable(user));

        //when
        User result = userService.findByEmail(target);

        //then
        assertNotNull(result);
        assertEquals(target, result.getEmail());
    }

    @Test
    @DisplayName("findByEmail: 데이터가 없는 경우")
    void findByEmail_success_empty() {
        //given
        String target = "abc@example.com";
        given(userRepository.findByEmail(target)).willReturn(Optional.empty());

        //when
        User result = userService.findByEmail(target);

        //then
        assertNull(result);
    }

    @Test
    @DisplayName("findByDisplayName: 데이터가 있는 경우")
    void findByDisplayName_success_existData() {
        //given
        String target = "display name";
        User user = User.builder()
                .email("abc@example.com")
                .password("password")
                .displayName(target)
                .build();

        given(userRepository.findByDisplayName(target)).willReturn(Optional.ofNullable(user));

        //when
        User result = userService.findByDisplayName(target);

        //then
        assertNotNull(result);
        assertEquals(target, result.getDisplayName());
    }

    @Test
    @DisplayName("findByDisplayName: 데이터가 있는 경우")
    void findByDisplayName_success_empty() {
        //given
        String target = "display name";
        given(userRepository.findByDisplayName(target)).willReturn(Optional.empty());

        //when
        User result = userService.findByDisplayName(target);

        //then
        assertNull(result);
    }

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