package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException(HttpStatus status, String message) {
        super(status, message);
    }

    public NotFoundException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }
}
