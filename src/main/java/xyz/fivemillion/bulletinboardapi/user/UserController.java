package xyz.fivemillion.bulletinboardapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.IllegalPasswordException;
import xyz.fivemillion.bulletinboardapi.error.LoginException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthenticationToken;
import xyz.fivemillion.bulletinboardapi.jwt.JwtTokenUtil;
import xyz.fivemillion.bulletinboardapi.user.dto.*;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import javax.validation.Valid;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.ApiResult;
import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.success;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(path = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<UserInfo> register(@Valid @RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);
            return success(HttpStatus.CREATED, new UserInfo(user));
        } catch (IllegalPasswordException e) {
            e.setHttpStatus(HttpStatus.BAD_REQUEST);
            throw e;
        } catch (DuplicateException e) {
            e.setHttpStatus(HttpStatus.CONFLICT);
            throw e;
        }

    }

    @PostMapping(path = "check/email")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> doubleCheckEmail(@Valid @RequestBody EmailCheckRequest request) {
        User user = userService.findByEmail(request.getEmail());
        return user == null ? success(HttpStatus.OK, true) : success(HttpStatus.OK, false);
    }

    @PostMapping(path = "check/displayname")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> doubleCheckDisplayName(@Valid @RequestBody DisplayNameCheckRequest request) {
        User user = userService.findByDisplayName(request.getDisplayName());
        return user == null ? success(HttpStatus.OK, true) : success(HttpStatus.OK, false);
    }

    @PostMapping(path = "login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new JwtAuthenticationToken(request.getEmail(), request.getPassword())
            );

            final User user = (User) authentication.getDetails();
            return success(HttpStatus.OK, jwtTokenUtil.generateJwtToken(user));
        } catch (LoginException e) {
            e.setHttpStatus(HttpStatus.BAD_REQUEST);
            throw e;
        }
    }
}
