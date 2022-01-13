package xyz.fivemillion.bulletinboardapi.post.comment.dto;

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
