package xyz.fivemillion.bulletinboardapi.post.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.NullException;
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
        checkNotNull(writer, Error.UNKNOWN_USER);
        checkNotNull(post, Error.UNKNOWN_POST);
        checkNotBlank(content, Error.CONTENT_IS_NULL_OR_BLANK);

        Comment comment = Comment.builder()
                .writer(writer)
                .post(post)
                .content(content)
                .build();

        try {
            commentRepository.save(comment);
        } catch (NullException e) {
            throw new NotFoundException(e.getError());
        }

        return comment;
    }
}
