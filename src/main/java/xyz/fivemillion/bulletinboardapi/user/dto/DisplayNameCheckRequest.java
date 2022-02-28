package xyz.fivemillion.bulletinboardapi.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ApiModel
public class DisplayNameCheckRequest {

    @NotBlank
    @Length(min = 6, max = 20, message = "the display name must be between 6 and 20 characters.")
    @ApiModelProperty(name = "displayName", example = "nickname", required = true, notes = "the display name must be between 6 and 20 characters.")
    private String displayName;

    public DisplayNameCheckRequest() {}

    public DisplayNameCheckRequest(String displayName) {
        this.displayName = displayName;
    }
}
