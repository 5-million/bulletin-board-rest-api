package xyz.fivemillion.bulletinboardapi.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import xyz.fivemillion.bulletinboardapi.user.User;
import xyz.fivemillion.bulletinboardapi.user.service.UserService;

import java.util.ArrayList;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return processUserAuthentication(
                (String) authenticationToken.getPrincipal(),
                authenticationToken.getCredentials()
        );
    }

    private Authentication processUserAuthentication(String email, String password) {
        try {
            User user = userService.login(email, password);
            JwtAuthenticationToken authenticated = new JwtAuthenticationToken(
                    new JwtAuthentication(user.getEmail(), user.getDisplayName()),
                    null,
                    new ArrayList<>()
            );
            authenticated.setDetails(user);
            return authenticated;
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}
