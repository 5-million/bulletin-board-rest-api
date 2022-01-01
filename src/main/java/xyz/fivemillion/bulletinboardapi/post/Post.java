package xyz.fivemillion.bulletinboardapi.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Length(min = 1, max = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    @NotBlank
    private String content;

    @Column
    private final long views = 0;

    @Column
    private final LocalDateTime createAt = LocalDateTime.now();

    @Column
    private final LocalDateTime updateAt = LocalDateTime.now();

    @Builder
    private Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }
}
