package xyz.fivemillion.bulletinboardapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.user.dto.DisplayNameCheckRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.EmailCheckRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserInfo;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import javax.validation.Valid;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<UserInfo> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        return success(HttpStatus.CREATED, new UserInfo(user));
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
}
