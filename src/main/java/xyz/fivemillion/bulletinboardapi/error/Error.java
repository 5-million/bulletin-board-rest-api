package xyz.fivemillion.bulletinboardapi.error;

public enum Error {

    EMAIL_DUPLICATE("이미 존재하는 이메일 입니다.", "이미 존재하는 이메일"),
    DISPLAY_NAME_DUPLICATE("이미 존재하는 닉네임입니다.", "이미 존재하는 닉네임"),
    CONFIRM_PASSWORD_NOT_MATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다.", "password != confirmPassword"),
    UNKNOWN_USER("등록되지 않은 사용자입니다.", "등록되지 않은 사용자의 요청"),
    UNKNOWN_POST("등록되지 않은 포스트입니다.", "등록되지 않은 포스트에 대한 요청"),
    UNKNOWN_COMMENT("등록되지 않은 댓글입니다.", "등록되지 않은 댓글에 대한 요청"),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", "등록되지 않은 사용자"),
    POST_NOT_FOUND("포스트를 찾을 수 없습니다.", "등록되지 않은 포스트"),
    EMAIL_AND_PASSWORD_NOT_MATCH("이메일과 비밀번호를 확인하세요.", "이메일과 비밀번호가 맞지 않음"),
    NOT_POST_OWNER("권한이 없습니다.", "포스트의 작성자가 아님"),
    NOT_COMMENT_OWNER("권한이 없습니다.", "댓글의 작성자가 아님"),
    CONTENT_IS_NULL_OR_BLANK("내용은 필수이며 최소 1자 이상이어야 합니다.", "content == null || content == blank"),
    REQUEST_DTO_IS_NULL("*Request 객체는 반드시 존재해야 합니다.", "request == null"),
    ;

    private final String message;
    private final String description;

    Error(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
