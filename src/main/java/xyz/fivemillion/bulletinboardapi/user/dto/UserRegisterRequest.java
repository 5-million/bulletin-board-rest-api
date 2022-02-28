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
public class UserRegisterRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    @ApiModelProperty(name = "email", example = "abc@example.com", required = true, notes = "the format must be email format.")
    private String email;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    @ApiModelProperty(
            name = "password",
            example = "11111111",
            required = true,
            notes = "The password must be at least 8 characters long.",
            position = 1
    )
    private String password;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    @ApiModelProperty(
            name = "password",
            example = "11111111",
            required = true,
            notes = "confirmPassword must match password.",
            position = 2
    )
    private String confirmPassword;

    @Length(min = 6, max = 20, message = "the display name must be between 6 and 20 characters.")
    @NotBlank
    @ApiModelProperty(
            name = "displayName",
            example = "nickname",
            required = true,
            notes = "the display name must be between 6 and 20 characters.",
            position = 3
    )
    private String displayName;

    public UserRegisterRequest() {}

    public UserRegisterRequest(String email, String password, String confirmPassword, String displayName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.displayName = displayName;
    }
}
