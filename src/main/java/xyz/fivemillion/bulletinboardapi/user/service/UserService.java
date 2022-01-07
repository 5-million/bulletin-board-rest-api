package xyz.fivemillion.bulletinboardapi.user.service;

import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.dto.LoginRequest;
import xyz.fivemillion.bulletinboardapi.user.dto.UserRegisterRequest;

public interface UserService {

    User register(UserRegisterRequest request);
    User findByEmail(String email);
    User findByDisplayName(String displayName);
    User login(String email, String password);
    User login(LoginRequest request);
}
