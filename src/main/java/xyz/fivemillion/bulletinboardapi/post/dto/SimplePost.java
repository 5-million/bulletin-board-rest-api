package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;

import java.time.LocalDateTime;

@Getter @Setter
public class SimplePost {

    protected Long postId;
    protected String title;
    protected String writer;
    protected Long views;
    protected Integer commentCount;
    protected LocalDateTime createAt;
    protected LocalDateTime updateAt;

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
