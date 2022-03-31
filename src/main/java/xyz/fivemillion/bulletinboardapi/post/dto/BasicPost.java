package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.category.dto.SimplePostCategory;

@Getter @Setter
public class BasicPost extends SimplePost{

    private SimplePostCategory category;

    public BasicPost(Post post) {
        super(post);
        this.category = new SimplePostCategory(post.getCategory());
    }
}
