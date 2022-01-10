package xyz.fivemillion.bulletinboardapi.jwt;

public class JwtAuthentication {

    private String email;
    private String displayName;

    public JwtAuthentication(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
