package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class DisplayNameDuplicateException extends CustomException {

    public DisplayNameDuplicateException(HttpStatus status, String message) {
        super(status, message);
    }

    public DisplayNameDuplicateException(String message) {
        this(HttpStatus.CONFLICT, message);
    }
}
