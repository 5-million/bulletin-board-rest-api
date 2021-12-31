package xyz.fivemillion.bulletinboardapi.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class EmailCheckRequest {

    @NotBlank
    @Email(message = "the format must be email format.")
    private String email;

    public EmailCheckRequest() {}

    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
