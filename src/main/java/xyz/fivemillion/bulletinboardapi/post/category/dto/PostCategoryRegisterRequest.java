package xyz.fivemillion.bulletinboardapi.post.category.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ApiModel
public class PostCategoryRegisterRequest {

    @NotBlank(message = "Must be at least 1 character.")
    @ApiModelProperty(name = "categoryName", example = "category", required = true)
    private String categoryName;

    @ApiModelProperty(name = "parentId")
    private Long parentId;

    public PostCategoryRegisterRequest(String categoryName, Long parentId) {
        this.categoryName = categoryName;
        this.parentId = parentId;
    }
}
