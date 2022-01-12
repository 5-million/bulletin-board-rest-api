package xyz.fivemillion.bulletinboardapi.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.repository.PostRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public Post register(User writer, PostRegisterRequest request) {
        if (writer.getId() == null)
            throw new UnAuthorizedException(Error.UNKNOWN_USER_REGISTER);

        try {
            Post post = Post.builder()
                    .writer(writer)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .build();

            postRepository.save(post);

            return post;
        } catch (IllegalStateException e) {
            throw new UnAuthorizedException(Error.UNKNOWN_USER_REGISTER);
        }
    }

    @Override
    public List<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable.getOffset(), pageable.getSize());
    }
}
