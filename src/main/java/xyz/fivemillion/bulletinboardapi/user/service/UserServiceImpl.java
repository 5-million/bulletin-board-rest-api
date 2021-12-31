package xyz.fivemillion.bulletinboardapi.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.error.DisplayNameDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.EmailDuplicateException;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.PasswordNotMatchException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.EncryptUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EncryptUtil encryptUtil;

    @Override
    @Transactional
    public User register(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new PasswordNotMatchException("password and confirm password do not match.");

        if (findByEmail(request.getEmail()) != null)
            throw new EmailDuplicateException("duplicate email.");

        if (findByDisplayName(request.getDisplayName()) != null)
            throw new DisplayNameDuplicateException("duplicate display name.");

        User user = User.builder()
                .email(request.getEmail())
                .password(encryptUtil.encrypt(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();

        userRepository.save(user);

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByDisplayName(String displayName) {
        return userRepository.findByDisplayName(displayName).orElse(null);
    }

    @Override
    public User login(LoginRequest request) {
        String LOGIN_FAIL_MESSAGE = "email or password is incorrect.";
        User user = findByEmail(request.getEmail());

        if (user == null)
            throw new NotFoundException(HttpStatus.BAD_REQUEST, LOGIN_FAIL_MESSAGE);

        if (!encryptUtil.isMatch(request.getPassword(), user.getPassword()))
            throw new PasswordNotMatchException(HttpStatus.BAD_REQUEST, LOGIN_FAIL_MESSAGE);

        return user;
    }
}
