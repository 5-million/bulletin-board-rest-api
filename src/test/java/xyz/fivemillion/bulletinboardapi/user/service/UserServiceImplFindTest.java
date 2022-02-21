package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

public class UserServiceImplFindTest extends UserServiceImplTest {

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
