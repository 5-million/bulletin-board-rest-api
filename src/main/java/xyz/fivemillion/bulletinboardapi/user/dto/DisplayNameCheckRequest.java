package xyz.fivemillion.bulletinboardapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class DisplayNameCheckRequest {

    @NotBlank
    @Length(min = 6, max = 20, message = "the display name must be between 6 and 20 characters.")
    private String displayName;

    public DisplayNameCheckRequest() {}

    public DisplayNameCheckRequest(String displayName) {
        this.displayName = displayName;
    }
}
