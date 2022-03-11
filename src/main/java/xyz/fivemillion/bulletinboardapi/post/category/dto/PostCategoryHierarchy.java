package xyz.fivemillion.bulletinboardapi.post.category.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PostCategoryHierarchy extends SimplePostCategory {

    private List<SimplePostCategory> subcategories;

    public PostCategoryHierarchy(PostCategory postCategory) {
        super(postCategory);

        subcategories = postCategory.getChild()
                .stream()
                .map(SimplePostCategory::new)
                .collect(Collectors.toList());
    }
}
