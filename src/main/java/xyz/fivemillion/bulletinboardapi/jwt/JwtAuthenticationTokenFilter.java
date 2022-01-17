package xyz.fivemillion.bulletinboardapi.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String authorizationToken = obtainAuthorizationToken(request);

                if (authorizationToken != null && jwtTokenUtil.verify(authorizationToken)) {
                    String email = jwtTokenUtil.getEmailFromToken(authorizationToken);
                    String displayName = jwtTokenUtil.getDisplayNameFromToken(authorizationToken);
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                            new JwtAuthentication(email, displayName),
                            null,
                            new ArrayList<>()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException exception) {
                request.setAttribute("exception", "invalid token");
            } catch (ExpiredJwtException exception) {
                request.setAttribute("exception", "expired token");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String obtainAuthorizationToken(HttpServletRequest request) {
        String authorizationTokenHeader = request.getHeader("X-FM-AUTH");

        String authorizationToken = null;
        if (authorizationTokenHeader != null && authorizationTokenHeader.startsWith("Bearer")) {
            authorizationToken = authorizationTokenHeader.substring(7);
        }

        return authorizationToken;
    }
}
