package xyz.fivemillion.bulletinboardapi.user;

import io.swagger.annotations.*;
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

@Api(tags = "사용자 관련 API")
@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "사용자 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "사용자 등록 성공"),
            @ApiResponse(code = 400, message = "사용자 등록 실패"),
            @ApiResponse(code = 409, message = "이메일 혹은 닉네임 중복")
    })
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

    @ApiOperation(value = "이메일 중복 체크")
    @PostMapping(path = "check/email")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> doubleCheckEmail(@Valid @RequestBody EmailCheckRequest request) {
        User user = userService.findByEmail(request.getEmail());
        return user == null ? success(HttpStatus.OK, true) : success(HttpStatus.OK, false);
    }

    @ApiOperation(value = "닉네임 중복 체크")
    @PostMapping(path = "check/displayname")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> doubleCheckDisplayName(@Valid @RequestBody DisplayNameCheckRequest request) {
        User user = userService.findByDisplayName(request.getDisplayName());
        return user == null ? success(HttpStatus.OK, true) : success(HttpStatus.OK, false);
    }

    @ApiOperation(value = "로그인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "로그인 실패(이메일 혹은 패스워드 오류)")
    })
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
