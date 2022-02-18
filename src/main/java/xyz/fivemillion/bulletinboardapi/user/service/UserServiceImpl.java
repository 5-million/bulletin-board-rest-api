package xyz.fivemillion.bulletinboardapi.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.error.DuplicateException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalPasswordException;
import xyz.fivemillion.bulletinboardapi.error.LoginException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new IllegalPasswordException(Error.CONFIRM_PASSWORD_NOT_MATCH);

        if (findByEmail(request.getEmail()) != null)
            throw new DuplicateException(Error.EMAIL_DUPLICATE);

        if (findByDisplayName(request.getDisplayName()) != null)
            throw new DuplicateException(Error.DISPLAY_NAME_DUPLICATE);

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
    public User login(String email, String password) {
        User user = findByEmail(email);

        if (user == null)
            throw new LoginException(Error.USER_NOT_FOUND);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new LoginException(Error.EMAIL_AND_PASSWORD_NOT_MATCH);

        return user;
    }

    @Override
    public User login(LoginRequest request) {
        return login(request.getEmail(), request.getPassword());
    }
}
