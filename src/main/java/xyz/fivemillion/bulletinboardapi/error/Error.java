package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum Error {
    // 1xx: 등록관련, 2xx: 인증관련, 3xx: 조회관련
    EMAIL_DUPLICATE(CONFLICT, "U101", "이미 존재하는 이메일입니다."),
    DISPLAY_NAME_DUPLICATE(CONFLICT, "U102", "이미 존재하는 닉네임입니다."),
    CONFIRM_PASSWORD_NOT_MATCH(BAD_REQUEST, "U103", "패스워드와 패스워드확인이 일치하지 않습니다"),
    USER_NOT_FOUND(NOT_FOUND, "U201", "존재하지 않는 사용자입니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "U202", "패스워드가 일치하지 않습니다."),
    UNKNOWN_USER(UNAUTHORIZED, "P201", "등록되지 않은 사용자의 요청입니다."),
    NOT_POST_WRITER(FORBIDDEN, "P202", "포스트 작성자만이 삭제할 수 있습니다."),
    POST_NOT_FOUND(NOT_FOUND, "P301", "존재하지 않는 포스트입니다."),
    UNKNOWN_USER_OR_POST(BAD_REQUEST, "C101", "등록되지 않은 사용자 또는 포스트에 대한 요청입니다."),
    CONTENT_IS_NULL_OR_BLANK(BAD_REQUEST, "C102", "내용이 공백 또는 존재하지 않습니다."),
    UNKNOWN_POST(BAD_REQUEST, "C103" , "존재하지 않는 포스트에 대한 요청입니다."),
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
