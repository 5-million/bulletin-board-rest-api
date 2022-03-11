package xyz.fivemillion.bulletinboardapi.post.category.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.category.PostCategory;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class BasicPostCategory extends SimplePostCategory {

    private List<SimplePostCategory> navigation;

    public BasicPostCategory(PostCategory postCategory) {
        super(postCategory);
        this.navigation = postCategory.getNavigation()
                .stream()
                .map(SimplePostCategory::new)
                .collect(Collectors.toList());
    }
}
