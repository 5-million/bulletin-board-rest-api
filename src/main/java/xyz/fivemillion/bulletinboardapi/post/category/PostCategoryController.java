package xyz.fivemillion.bulletinboardapi.post.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.jwt.JwtAuthentication;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryDto;
import xyz.fivemillion.bulletinboardapi.post.category.dto.PostCategoryRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.category.service.PostCategoryService;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil.ApiResult;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.success;

@Api(tags = "카테고리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/category")
public class PostCategoryController {

    private final PostCategoryService postCategoryService;
    private final UserService userService;

    @ApiOperation(value = "카테고리 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "카테고리 등록 성공"),
            @ApiResponse(code = 400, message = "상위 카테고리 존재하지 않아 등록 실패"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 409, message = "이미 등록된 카테고리")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<PostCategoryDto> register(
            @Valid @RequestBody PostCategoryRegisterRequest request,
            @AuthenticationPrincipal JwtAuthentication authentication) {
        if (userService.findByEmail(authentication.getEmail()) == null)
            throw new NotFoundException(Error.UNKNOWN_USER, HttpStatus.UNAUTHORIZED);

        try {
            return success(HttpStatus.CREATED, new PostCategoryDto(postCategoryService.register(request)));
        } catch (DuplicateException e) {
            e.setHttpStatus(HttpStatus.CONFLICT);
            throw e;
        } catch (NotFoundException e) {
            e.setHttpStatus(HttpStatus.BAD_REQUEST);
            throw e;
        }
    }

    @ApiOperation(value = "모든 카테고리 조회(계층구조)")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<List<PostCategoryDto>> getAll() {
        return success(
                HttpStatus.OK,
                postCategoryService.findAll()
                        .stream()
                        .map(PostCategoryDto::new).collect(Collectors.toList())
        );
    }

}
