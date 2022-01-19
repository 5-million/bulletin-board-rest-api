package xyz.fivemillion.bulletinboardapi.post.comment.dto;

import javax.validation.constraints.NotBlank;

public class CommentRegisterRequest {

    @NotBlank
    private String content;

    public CommentRegisterRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
