package xyz.fivemillion.bulletinboardapi.post.category.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PostCategoryDto extends SimplePostCategoryDto {

    private List<PostCategoryDto> subCategory;

    public PostCategoryDto(PostCategory postCategory) {
        super(postCategory);
        setSubCategory(postCategory);
    }

    private void setSubCategory(PostCategory postCategory) {
        this.subCategory = postCategory.getChild().stream().map(PostCategoryDto::new).collect(Collectors.toList());
    }
}
