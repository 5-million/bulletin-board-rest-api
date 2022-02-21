package xyz.fivemillion.bulletinboardapi.user.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.fivemillion.bulletinboardapi.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    protected final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Mock protected UserRepository userRepository;
    @Mock protected PasswordEncoder passwordEncoder;
    @InjectMocks protected UserServiceImpl userService;

}