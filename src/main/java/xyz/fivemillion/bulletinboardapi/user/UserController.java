package xyz.fivemillion.bulletinboardapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.DisplayNameDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.EmailDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.PasswordNotMatchException;
import xyz.fivemillion.bulletinboardapi.user.dto.UserInfo;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil;

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
}
