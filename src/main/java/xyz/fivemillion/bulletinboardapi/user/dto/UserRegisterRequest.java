package xyz.fivemillion.bulletinboardapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UserRegisterRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    private String email;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    private String password;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    private String confirmPassword;

    @Length(min = 6, max = 20, message = "the display name must be between 6 and 20 characters.")
    @NotBlank
    private String displayName;

    public UserRegisterRequest(String email, String password, String confirmPassword, String displayName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.displayName = displayName;
    }
}
