package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final Error error;

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public String getMessage() {
        return error.getMessage();
    }

    public String getErrorCode() {
        return error.getCode();
    }

    public HttpStatus getHttpStatus() {
        return error.getStatus();
    }
}
