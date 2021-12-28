package xyz.fivemillion.bulletinboardapi.user;

import java.util.Optional;

public interface UserRepository {

    void save(User user);
    Optional<User> findByEmail(String target);
    Optional<User> findByDisplayName(String target);
}
