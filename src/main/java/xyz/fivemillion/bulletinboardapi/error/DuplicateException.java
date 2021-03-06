package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class DuplicateException extends CustomException {

    public DuplicateException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public DuplicateException(Error error) {
        this(error, null);
    }
}
