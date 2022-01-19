package xyz.fivemillion.bulletinboardapi.post.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalArgumentException;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.post.comment.repository.CommentRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import static xyz.fivemillion.bulletinboardapi.utils.CheckUtil.checkNotBlank;
import static xyz.fivemillion.bulletinboardapi.utils.CheckUtil.checkNotNull;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment register(User writer, Post post, String content) throws Exception {
        checkNotNull(writer, UnAuthorizedException.class, Error.UNKNOWN_USER);
        checkNotNull(post, NotFoundException.class, Error.POST_NOT_FOUND);
        checkNotBlank(content, IllegalArgumentException.class, Error.CONTENT_IS_NULL_OR_BLANK);

        Comment comment = Comment.builder()
                .writer(writer)
                .post(post)
                .content(content)
                .build();

        commentRepository.save(comment);

        return comment;
    }
}
