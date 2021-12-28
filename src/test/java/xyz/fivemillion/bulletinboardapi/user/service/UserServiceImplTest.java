package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import xyz.fivemillion.bulletinboardapi.error.DisplayNameDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.EmailDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.PasswordNotMatchException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.BCryptEncryption;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.EncryptUtil;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private BCryptEncryption bCryptEncryption = new BCryptEncryption();
    @Mock UserRepository userRepository;
    @Mock EncryptUtil encryptUtil;
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
        PasswordNotMatchException exception =
                assertThrows(PasswordNotMatchException.class, () -> userService.register(request));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("password and confirm password do not match.", exception.getMessage());
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
        EmailDuplicateException thrown = assertThrows(
                EmailDuplicateException.class,
                () -> userService.register(request)
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
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
        DisplayNameDuplicateException thrown = assertThrows(
                DisplayNameDuplicateException.class,
                () -> userService.register(request)
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    @DisplayName("register fail: repository에서 중복 예외 던지는 경우")
    void register_fail_repositoryThrownPersistenceException() {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@example.com",
                "password",
                "password",
                "display name"
        );

        doThrow(PersistenceException.class).when(userRepository).save(any(User.class));

        //when
        assertThrows(PersistenceException.class, () -> userService.register(request));
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
        given(encryptUtil.encrypt("password")).willReturn(bCryptEncryption.encrypt("password"));

        //when
        User result = userService.register(request);

        //then
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getDisplayName(), result.getDisplayName());
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
}