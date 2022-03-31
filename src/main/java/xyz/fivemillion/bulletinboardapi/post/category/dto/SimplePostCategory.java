package xyz.fivemillion.bulletinboardapi.post.category.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

@Getter @Setter
public class SimplePostCategory {

    protected Long id;
    protected String category;
    protected Integer depth;
    protected String group;

    public SimplePostCategory(PostCategory postCategory) {
        this.id = postCategory.getId();
        this.category = postCategory.getCategoryName();
        this.depth = postCategory.getDepth();
        this.group = postCategory.getGroupName();
    }
}
