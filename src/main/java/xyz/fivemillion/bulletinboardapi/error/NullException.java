package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class NullException extends CustomException {

    public NullException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public NullException(Error error) {
        this(error, null);
    }
}
