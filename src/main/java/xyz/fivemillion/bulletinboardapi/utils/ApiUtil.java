package xyz.fivemillion.bulletinboardapi.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ApiUtil {

    public static <T> ApiResult<T> success(HttpStatus status, T data) {
        return new ApiResult<>(status, data, null);
    }

    public static ApiResult<?> fail(HttpStatus status, String message) {
        return new ApiResult<>(status, null, new ApiError(message));
    }

    public static ApiResult<?> fail(HttpStatus status, Throwable throwable) {
        return new ApiResult<>(status, null, new ApiError(throwable));
    }

    @Getter
    public static class ApiError {
        private final String message;

        public ApiError(String message) {
            this.message = message;
        }

        public ApiError(Throwable throwable) {
            this(throwable.getMessage());
        }
    }

    @Getter
    public static class ApiResult<T> {
        private final HttpStatus status;
        private final T response;
        private final ApiError error;

        public ApiResult(HttpStatus status, T response, ApiError error) {
            this.status = status;
            this.response = response;
            this.error = error;
        }
    }
}
