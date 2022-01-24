package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class IllegalPasswordException extends CustomException {

    public IllegalPasswordException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public IllegalPasswordException(Error error) {
        super(error);
    }
}
