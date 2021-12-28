package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

public interface CustomException {

    String getMessage();
    HttpStatus getStatus();
}
