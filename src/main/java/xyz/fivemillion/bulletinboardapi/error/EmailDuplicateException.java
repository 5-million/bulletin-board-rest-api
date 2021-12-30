package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class EmailDuplicateException extends CustomException {

    public EmailDuplicateException(HttpStatus status, String message) {
        super(status, message);
    }

    public EmailDuplicateException(String message) {
        this(HttpStatus.CONFLICT, message);
    }
}
