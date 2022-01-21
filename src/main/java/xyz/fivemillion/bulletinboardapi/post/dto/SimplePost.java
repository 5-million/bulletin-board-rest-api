package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;

import java.time.LocalDateTime;

@Getter @Setter
public class SimplePost {
    private Long postId;
    private String title;
    private String writer;
    private Long views;
    private Integer commentCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public SimplePost() {/*Empty*/}

    public SimplePost(Long postId, String title, String writer, Long views, Integer commentCount, LocalDateTime createAt, LocalDateTime updateAt) {
        this.postId = postId;
        this.title = title;
        this.writer = writer;
        this.views = views;
        this.commentCount = commentCount;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public SimplePost(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter().getDisplayName();
        this.views = post.getViews();
        this.commentCount = post.getComments().size();
        this.createAt = post.getCreateAt();
        this.updateAt = post.getUpdateAt();
    }
}
