package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
public class PostRegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 1, max = 100)
    private String title;

    @NotBlank
    private String content;

    public PostRegisterRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
