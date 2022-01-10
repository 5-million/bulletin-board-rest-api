package xyz.fivemillion.bulletinboardapi.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthentication;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.dto.SimplePost;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil.ApiResult;

import javax.validation.Valid;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.success;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<SimplePost> register(
            @Valid @RequestBody PostRegisterRequest request,
            @AuthenticationPrincipal JwtAuthentication authentication) {
        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null) throw new NotFoundException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자의 요청입니다.");

        return success(HttpStatus.CREATED, new SimplePost(postService.register(writer, request)));
    }
}
