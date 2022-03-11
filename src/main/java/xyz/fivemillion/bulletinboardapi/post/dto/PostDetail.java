package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.category.dto.BasicPostCategory;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.SimpleComment;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PostDetail extends SimplePost {

    private String content;
    private List<SimpleComment> comments;
    private BasicPostCategory category;


    public PostDetail(Post post) {
        super(post);
        this.content = post.getContent();
        this.comments = post.getComments().stream().map(SimpleComment::new).collect(Collectors.toList());
        this.category = new BasicPostCategory(post.getCategory());
    }
}
