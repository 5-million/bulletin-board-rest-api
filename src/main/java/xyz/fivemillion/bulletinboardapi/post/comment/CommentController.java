package xyz.fivemillion.bulletinboardapi.post.comment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
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

@Api(tags = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    @ApiOperation(value = "댓글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 등록 성공"),
            @ApiResponse(code = 400, message = "댓글 등록 실패"),
            @ApiResponse(code = 401, message = "등록되지 않은 사용자의 요청")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<SimpleComment> register(
            @RequestBody @Valid CommentRegisterRequest request,
            @AuthenticationPrincipal JwtAuthentication authentication) throws Exception {
        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null)
            throw new NotFoundException(Error.UNKNOWN_USER, HttpStatus.UNAUTHORIZED);

        try {
            Post target = postService.findById(request.getPostId());
            Comment comment = commentService.register(writer, target, request.getContent());

            return ApiUtil.success(HttpStatus.CREATED, new SimpleComment(comment));
        } catch (NotFoundException e) {
            throw new NotFoundException(Error.UNKNOWN_POST, HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 삭제 성공"),
            @ApiResponse(code = 400, message = "댓글 삭제 실패"),
            @ApiResponse(code = 401, message = "등록되지 않은 사용자의 요청"),
            @ApiResponse(code = 403, message = "댓글 작성자가 아닌 사용자의 요청")
    })
    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable final Long id,
            @AuthenticationPrincipal JwtAuthentication authentication) {
        User requester = userService.findByEmail(authentication.getEmail());
        if (requester == null)
            throw new NotFoundException(Error.UNKNOWN_USER, HttpStatus.UNAUTHORIZED);

        try {
            commentService.delete(requester, id);
        } catch (NotFoundException e) {
            e.setHttpStatus(HttpStatus.BAD_REQUEST);
            throw e;
        } catch (NotOwnerException e) {
            e.setHttpStatus(HttpStatus.FORBIDDEN);
            throw e;
        }
    }
}
