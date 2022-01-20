package xyz.fivemillion.bulletinboardapi.post.comment.dto;

import xyz.fivemillion.bulletinboardapi.post.comment.Comment;

import java.time.LocalDateTime;

public class SimpleComment {

    private Long commentId;
    private String writer;
    private Long postId;
    private String content;
    private LocalDateTime createAt;

    public SimpleComment() {/*Empty*/}

    public SimpleComment(Long commentId, String writer, Long postId, String content, LocalDateTime createAt) {
        this.commentId = commentId;
        this.writer = writer;
        this.postId = postId;
        this.content = content;
        this.createAt = createAt;
    }

    public SimpleComment(Comment comment) {
        this.commentId = comment.getId();
        this.writer = comment.getWriter().getDisplayName();
        this.postId = comment.getPost().getId();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getWriter() {
        return writer;
    }

    public Long getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
