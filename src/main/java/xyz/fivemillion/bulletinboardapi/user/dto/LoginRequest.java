package xyz.fivemillion.bulletinboardapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class LoginRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    private String email;

    @NotBlank
    @Length(min = 8, message = "The password must be at least 8 characters long.")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
