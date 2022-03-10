package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.category.dto.SimplePostCategoryDto;
import xyz.fivemillion.bulletinboardapi.post.comment.dto.SimpleComment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PostDetail extends SimplePost {

    private String content;
    private List<SimpleComment> comments = new ArrayList<>();
    private List<SimplePostCategoryDto> navigation;

    public PostDetail() {
        super();
    }

    public PostDetail(Post post) {
        super(post);
        this.content = post.getContent();
        this.comments = post.getComments().stream().map(SimpleComment::new).collect(Collectors.toList());
        this.navigation = post.getCategory().getNavigation().stream().map(SimplePostCategoryDto::new).collect(Collectors.toList());
    }
}
