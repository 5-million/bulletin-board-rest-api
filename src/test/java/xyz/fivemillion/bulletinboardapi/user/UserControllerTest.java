package xyz.fivemillion.bulletinboardapi.user;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.fivemillion.bulletinboardapi.error.ControllerExceptionHandler;
import xyz.fivemillion.bulletinboardapi.error.DisplayNameDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.EmailDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.PasswordNotMatchException;
import xyz.fivemillion.bulletinboardapi.user.dto.DisplayNameCheckRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.EmailCheckRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import javax.persistence.PersistenceException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {



    @Mock UserService userService;
    @InjectMocks private UserController userController;

    private MockMvc mvc;
    private Gson gson;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .alwaysDo(print())
                .build();
        gson = new Gson();
    }

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
                .willThrow(new EmailDuplicateException("duplicate email"));

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/register")
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isConflict());
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
                .willThrow(new DisplayNameDuplicateException("duplicate display name"));

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpect(status().isConflict());
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
                .willThrow(new PasswordNotMatchException("password not match"));

        //when
        ResultActions result = performRegister(request);

        //then
        result.andExpectAll(status().isBadRequest());
    }

    @Test
    @DisplayName("register fail: 등록과정에서 PersistenceException 발생하는 경우")
    void register_fail_throwPersistenceException() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "abc@test.com",
                "password",
                "password",
                "display name"
        );

        given(userService.register(any(UserRegisterRequest.class))).willThrow(PersistenceException.class);

        //when
        ResultActions result = performRegister(request);

        //then
        result.andDo(print());
        result.andExpect(status().is5xxServerError());
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