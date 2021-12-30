package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends CustomException {

    public PasswordNotMatchException(HttpStatus status, String message) {
        super(status, message);
    }

    public PasswordNotMatchException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }
}
