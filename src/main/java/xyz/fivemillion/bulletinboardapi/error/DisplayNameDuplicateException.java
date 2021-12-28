package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class DisplayNameDuplicateException extends RuntimeException implements CustomException {

    private final HttpStatus status;

    public DisplayNameDuplicateException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
