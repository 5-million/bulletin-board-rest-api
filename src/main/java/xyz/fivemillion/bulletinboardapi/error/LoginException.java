package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class LoginException extends CustomException {

    public static final String LOGIN_FAIL_MESSAGE = "이메일 또는 비밀번호를 확인해주세요.";
    private final HttpStatus LOGIN_FAIL_HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public LoginException(Error error) {
        super(error);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return LOGIN_FAIL_HTTP_STATUS;
    }

    @Override
    public String getMessage() {
        return LOGIN_FAIL_MESSAGE;
    }
}
