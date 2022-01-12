package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum Error {
    // 1xx: 등록관련, 2xx: 인증관련
    EMAIL_DUPLICATE(CONFLICT, "U101", "이미 존재하는 이메일입니다."),
    DISPLAY_NAME_DUPLICATE(CONFLICT, "U102", "이미 존재하는 닉네임입니다."),
    CONFIRM_PASSWORD_NOT_MATCH(BAD_REQUEST, "U103", "패스워드와 패스워드확인이 일치하지 않습니다"),
    USER_NOT_FOUND(NOT_FOUND, "U201", "존재하지 않는 사용자입니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "U202", "패스워드가 일치하지 않습니다."),
    UNKNOWN_USER_REGISTER(UNAUTHORIZED, "P201", "등록되지 않은 사용자의 포스트 등록입니다."),
    ;

    private HttpStatus status;
    private String code;
    private String message;

    Error(HttpStatus status, String code, String message) {
        this.status =status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
