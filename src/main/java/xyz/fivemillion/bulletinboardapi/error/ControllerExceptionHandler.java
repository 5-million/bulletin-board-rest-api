package xyz.fivemillion.bulletinboardapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.fivemillion.bulletinboardapi.utils.ApiUtil;

import javax.persistence.PersistenceException;

import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.fail;

@ControllerAdvice
public class ControllerExceptionHandler {

    private ResponseEntity<ApiUtil.ApiResult<?>> newResponse(CustomException exception) {
        return new ResponseEntity<>(fail(exception.getHttpStatus(), exception.getMessage()), exception.getHttpStatus());
    }

    private ResponseEntity<ApiUtil.ApiResult<?>> newResponse(HttpStatus status, Throwable throwable) {
        return new ResponseEntity<>(fail(status, throwable), status);
    }

    private ResponseEntity<ApiUtil.ApiResult<?>> newResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(fail(status, message), status);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiUtil.ApiResult<?>> handleCustomException(CustomException exception) {
        return newResponse(exception);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiUtil.ApiResult<?>> handleBadRequestException(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            return newResponse(
                    HttpStatus.BAD_REQUEST,
                    ((MethodArgumentNotValidException) exception).getBindingResult()
                            .getAllErrors()
                            .get(0)
                            .getDefaultMessage()
            );
        }

        return newResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler({PersistenceException.class})
    public ResponseEntity<ApiUtil.ApiResult<?>> handleInternalServerError(Exception exception) {
        return newResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ApiUtil.ApiResult<?>> handleException(Exception exception) {
        return newResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }
}
