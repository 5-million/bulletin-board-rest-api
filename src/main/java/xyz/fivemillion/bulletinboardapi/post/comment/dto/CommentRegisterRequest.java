package xyz.fivemillion.bulletinboardapi.post.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentRegisterRequest {

    @NotNull
    private Long postId;

    @NotBlank
    private String content;

    public CommentRegisterRequest(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }
}
