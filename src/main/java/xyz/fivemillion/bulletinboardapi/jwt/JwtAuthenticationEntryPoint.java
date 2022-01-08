package xyz.fivemillion.bulletinboardapi.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("content-type", "application/json");
        response.getWriter().write(unauthorized(request));
        response.getWriter().flush();
        response.getWriter().close();
    }

    private String unauthorized(HttpServletRequest request) throws JsonProcessingException {
        String message = (String) request.getAttribute("exception");
        return new ObjectMapper().writeValueAsString(ApiUtil.fail(HttpStatus.UNAUTHORIZED, message));
    }
}
