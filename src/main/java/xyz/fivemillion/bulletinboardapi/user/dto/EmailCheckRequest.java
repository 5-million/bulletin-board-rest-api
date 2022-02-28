package xyz.fivemillion.bulletinboardapi.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ApiModel
public class EmailCheckRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    @ApiModelProperty(name = "email", example = "abc@example.com", required = true, notes = "the format must be email format.")
    private String email;

    public EmailCheckRequest() {}

    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
