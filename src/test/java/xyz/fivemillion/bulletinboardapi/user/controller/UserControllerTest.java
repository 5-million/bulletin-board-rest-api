package xyz.fivemillion.bulletinboardapi.user.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.fivemillion.bulletinboardapi.error.ControllerExceptionHandler;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.user.UserController;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
abstract class UserControllerTest {

    @Mock protected UserService userService;
    @Mock protected AuthenticationManager authenticationManager;
    @Spy protected JwtTokenUtil jwtTokenUtil;
    @InjectMocks protected UserController userController;

    protected MockMvc mvc;
    protected Gson gson;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .alwaysDo(print())
                .build();
        gson = new Gson();

        ReflectionTestUtils.setField(jwtTokenUtil, "issuer", "5million");
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", "secret");
    }
}