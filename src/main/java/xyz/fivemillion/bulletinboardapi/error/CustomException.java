package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final Error error;
    private HttpStatus httpStatus;

    public CustomException(Error error, HttpStatus httpStatus) {
        super(error.getMessage());
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
        this.httpStatus = null;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Error getError() {
        return error;
    }

    public String getMessage() {
        return error.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
