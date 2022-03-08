package xyz.fivemillion.bulletinboardapi.post.category.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

@Getter @Setter
public class SimplePostCategoryDto {

    private Long id;
    private String category;
    private Integer depth;
    private String group;
    private SimplePostCategoryDto parent;

    public SimplePostCategoryDto(PostCategory postCategory) {
        this.id = postCategory.getId();
        this.category = postCategory.getCategoryName();
        this.depth = postCategory.getDepth();
        this.group = postCategory.getGroupName();
        this.parent = postCategory.getParent() == null ? null : new SimplePostCategoryDto(postCategory.getParent());
    }
}
