package xyz.fivemillion.bulletinboardapi.post;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NotOwnerException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthentication;
import xyz.fivemillion.bulletinboardapi.post.dto.PostDetail;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.dto.SimplePost;
import xyz.fivemillion.bulletinboardapi.post.service.PostService;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil.ApiResult;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.success;

@Api(tags = "포스트 관련 API")
@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @ApiOperation(value = "포스트 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "포스트 등록 성공"),
            @ApiResponse(code = 400, message = "포스트 등록 실패"),
            @ApiResponse(code = 401, message = "등록되지 않은 사용자의 요청")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<SimplePost> register(
            @Valid @RequestBody PostRegisterRequest request,
            @AuthenticationPrincipal JwtAuthentication authentication) {
        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null)
            throw new NotFoundException(Error.UNKNOWN_USER, HttpStatus.UNAUTHORIZED);

        return success(HttpStatus.CREATED, new SimplePost(postService.register(writer, request)));
    }

    @ApiOperation(value = "모든 포스트 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<List<SimplePost>> getAll(Pageable pageable) {
        List<SimplePost> posts = postService.findAll(pageable)
                .stream()
                .map(SimplePost::new)
                .collect(Collectors.toList());

        return success(HttpStatus.OK, posts);
    }

    @ApiOperation(value = "포스트 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 조회 성공"),
            @ApiResponse(code = 404, message = "등록되지 않은 포스트")
    })
    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<PostDetail> getById(@ApiParam(name = "id", value = "조회 대상 포스트의 id", type = "long") @PathVariable final Long id) {
        if (id < 1)
            throw new NotFoundException(Error.POST_NOT_FOUND, HttpStatus.NOT_FOUND);

        Post post = postService.findById(id);
        postService.increaseView(post);
        return success(HttpStatus.OK, new PostDetail(post));
    }

    @ApiOperation(value = "포스트 삭제")
    @ApiResponses({
            @ApiResponse(code = 204, message = "포스트 삭제 성공"),
            @ApiResponse(code = 400, message = "포스트 삭제 실패"),
            @ApiResponse(code = 401, message = "등록되지 않은 사용자의 요청"),
            @ApiResponse(code = 403, message = "포스트 작성자가 아닌 사용자의 요청"),
    })
    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@ApiParam(name = "id", value = "삭제 대상 포스트의 id", type = "long") @PathVariable final Long id,
                       @AuthenticationPrincipal JwtAuthentication authentication) {
        if (id < 1)
            throw new NotFoundException(Error.UNKNOWN_POST, HttpStatus.BAD_REQUEST);

        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null)
            throw new NotFoundException(Error.UNKNOWN_USER, HttpStatus.UNAUTHORIZED);

        try {
            postService.delete(writer, id);
        } catch (NotFoundException e) {
            throw new NotFoundException(Error.UNKNOWN_POST, HttpStatus.BAD_REQUEST);
        } catch (NotOwnerException e) {
            e.setHttpStatus(HttpStatus.FORBIDDEN);
            throw e;
        }
    }

    @ApiOperation(value = "포스트 검색")
    @GetMapping(path = "search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<List<SimplePost>> search(
            @RequestParam(value = "q", required = false) final String query,
            final Pageable pageable) {
        if (query == null)
            return getAll(pageable);

        List<SimplePost> posts = postService.findByQuery(query, pageable)
                .stream()
                .map(SimplePost::new)
                .collect(Collectors.toList());

        return success(HttpStatus.OK, posts);
    }
}
