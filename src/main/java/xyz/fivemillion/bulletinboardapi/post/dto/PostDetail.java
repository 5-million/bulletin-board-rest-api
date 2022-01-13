package xyz.fivemillion.bulletinboardapi.post.dto;

import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.SimpleComment;

import java.util.ArrayList;
import java.util.List;

public class PostDetail extends SimplePost {

    private String content;
    private List<SimpleComment> comments = new ArrayList<>();

    public PostDetail() {
        super();
    }

    public PostDetail(Post post) {
        super(post);
        this.content = post.getContent();
    }

    public String getContent() {
        return content;
    }

    public List<SimpleComment> getComments() {
        return comments;
    }
}
