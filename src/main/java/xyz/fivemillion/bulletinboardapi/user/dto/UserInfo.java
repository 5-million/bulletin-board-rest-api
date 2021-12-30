package xyz.fivemillion.bulletinboardapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.time.LocalDateTime;

@Getter @Setter
public class UserInfo {

    private String email;
    private String displayName;
    private LocalDateTime createAt;

    public UserInfo(String email, String displayName, LocalDateTime createAt) {
        this.email = email;
        this.displayName = displayName;
        this.createAt = createAt;
    }

    public UserInfo(User user) {
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.createAt = user.getCreateAt();
    }
}
