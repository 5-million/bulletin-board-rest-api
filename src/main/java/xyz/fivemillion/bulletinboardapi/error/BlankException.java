package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class BlankException extends CustomException {

    public BlankException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public BlankException(Error error) {
        this(error, null);
    }
}
