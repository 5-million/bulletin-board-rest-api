package xyz.fivemillion.bulletinboardapi.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
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
        if (writer == null) throw new UnAuthorizedException(Error.UNKNOWN_USER);

        return success(HttpStatus.CREATED, new SimplePost(postService.register(writer, request)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<List<SimplePost>> getAll(Pageable pageable) {
        List<SimplePost> posts = postService.findAll(pageable)
                .stream()
                .map(SimplePost::new)
                .collect(Collectors.toList());

        return success(HttpStatus.OK, posts);
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<PostDetail> getById(@PathVariable final Long id) {
        if (id < 1)
            throw new NotFoundException(Error.POST_NOT_FOUND);

        return success(HttpStatus.OK, new PostDetail(postService.findById(id)));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long id, @AuthenticationPrincipal JwtAuthentication authentication) {
        if (id < 1)
            throw new NotFoundException(Error.POST_NOT_FOUND);

        User writer = userService.findByEmail(authentication.getEmail());
        if (writer == null)
            throw new UnAuthorizedException(Error.UNKNOWN_USER);

        postService.delete(writer, id);
    }

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
