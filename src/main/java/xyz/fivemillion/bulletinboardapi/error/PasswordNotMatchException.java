package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends RuntimeException implements CustomException {

    private final HttpStatus status;

    public PasswordNotMatchException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public PasswordNotMatchException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
