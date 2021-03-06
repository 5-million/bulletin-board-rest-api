package xyz.fivemillion.bulletinboardapi.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false)
    private User writer;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Length(min = 1, max = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    @NotBlank
    private String content;

    @Column
    private long views = 0;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PostCategory category;

    @Column
    private final LocalDateTime createAt = LocalDateTime.now();

    @Column
    private LocalDateTime updateAt = LocalDateTime.now();

    @Builder
    private Post(Long id, User writer, String title, String content, PostCategory category) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseView() {
        this.views += 1;
    }
}
