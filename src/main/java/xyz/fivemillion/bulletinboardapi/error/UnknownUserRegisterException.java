package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class UnknownUserRegisterException extends CustomException {

    public UnknownUserRegisterException(HttpStatus status, String message) {
        super(status, message);
    }

    public UnknownUserRegisterException(String message) {
        this(HttpStatus.FORBIDDEN, message);
    }
}
