package xyz.fivemillion.bulletinboardapi.post.category;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.fivemillion.bulletinboardapi.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category")
    private String categoryName;

    @Column
    private String groupName;

    @Column
    private int depth = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostCategory parent;

    @OneToMany(mappedBy = "parent")
    private final List<PostCategory> child = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Post> posts = new ArrayList<>();

    @Builder
    public PostCategory(Long id, String categoryName, PostCategory parent) {
        this.id = id;
        this.categoryName = categoryName;
        this.parent = parent;

        if (parent == null) {
            this.groupName = categoryName;
            this.depth = 0;
        } else {
            this.groupName = parent.getGroupName();
            this.depth = parent.getDepth() + 1;
        }
    }

    public void addChild(PostCategory child) {
        this.child.add(child);
    }

    public List<PostCategory> getNavigation() {
        List<PostCategory> navigation;

        if (this.getParent() == null) navigation = new ArrayList<>();
        else navigation = this.getParent().getNavigation();

        navigation.add(this);
        return navigation;
    }
}
