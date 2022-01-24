package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public NotFoundException(Error error) {
        super(error);
    }
}
