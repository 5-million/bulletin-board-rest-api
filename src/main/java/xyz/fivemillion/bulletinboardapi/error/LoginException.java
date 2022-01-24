package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class LoginException extends CustomException {

    public LoginException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public LoginException(Error error) {
        super(error);
    }
}
