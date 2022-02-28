package xyz.fivemillion.bulletinboardapi.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ApiModel
public class LoginRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    @ApiModelProperty(name = "email", example = "abc@example.com", required = true, notes = "the format must be email format.")
    private String email;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    @ApiModelProperty(name = "password", example = "11111111", required = true, notes = "The password must be at least 8 characters long.")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
