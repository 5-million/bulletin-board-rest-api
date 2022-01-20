package xyz.fivemillion.bulletinboardapi.post.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthentication;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.CommentRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.SimpleComment;
import xyz.fivemillion.bulletinboardapi.post.comment.service.CommentService;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil.ApiResult;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<SimpleComment> register(
            @RequestBody @Valid CommentRegisterRequest request,
            @AuthenticationPrincipal JwtAuthentication authentication) throws Exception {
        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null) throw new UnAuthorizedException(Error.UNKNOWN_USER);

        try {
            Post target = postService.findById(request.getPostId());
            Comment comment = commentService.register(writer, target, request.getContent());

            return ApiUtil.success(HttpStatus.CREATED, new SimpleComment(comment));
        } catch (NotFoundException e) {
            throw new NotFoundException(Error.UNKNOWN_POST);
        }
    }
}
