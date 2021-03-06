package xyz.fivemillion.bulletinboardapi.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email(message = "the format must be email format.")
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(length = 20, nullable = false, unique = true)
    @Length(min = 6, max = 20, message = "the display name must be between 6 and 20 characters.")
    @NotBlank
    private String displayName;

    @Column
    private final LocalDateTime createAt = LocalDateTime.now();

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    private User(Long id, String email, String password, String displayName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
