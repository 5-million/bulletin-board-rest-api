package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import xyz.fivemillion.bulletinboardapi.error.PasswordNotMatchException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;
import xyz.fivemillion.bulletinboardapi.user.dto.UserInfo;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.service.UserServiceImpl;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.BCryptEncryption;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.EncryptUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private BCryptEncryption bCryptEncryption = new BCryptEncryption();
    @Mock
    UserRepository userRepository;
    @Mock EncryptUtil encryptUtil;
    @InjectMocks
    UserServiceImpl userService;

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
    void register_success() {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@email.com",
                "password",
                "password",
                "display name"
        );

        given(encryptUtil.encrypt("password")).willReturn(bCryptEncryption.encrypt("password"));

        //when
        UserInfo result = userService.register(request);

        //then
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getDisplayName(), result.getDisplayName());
        assertNotNull(result.getCreateAt());
    }
}