package xyz.fivemillion.bulletinboardapi.post.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
@ApiModel
public class PostRegisterRequest {

    @NotBlank
    @Length(min = 1, max = 100)
    @ApiModelProperty(name = "title", example = "title", required = true, position = 0)
    private String title;

    @NotBlank
    @ApiModelProperty(name = "content", example = "content", required = true, position = 1)
    private String content;

    public PostRegisterRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
