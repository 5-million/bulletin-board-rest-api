package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class NotOwnerException extends CustomException {

    public NotOwnerException(Error error, HttpStatus httpStatus) {
        super(error, httpStatus);
    }

    public NotOwnerException(Error error) {
        super(error);
    }
}
