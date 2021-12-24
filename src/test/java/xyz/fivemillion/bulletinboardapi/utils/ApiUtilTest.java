package xyz.fivemillion.bulletinboardapi.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.fivemillion.bulletinboardapi.utils.ApiUtil.fail;

class ApiUtilTest {

    @Test
    void success() {
        //given
        HttpStatus status = HttpStatus.CREATED;
        String data = "data";

        //when
        ApiUtil.ApiResult<String> result = ApiUtil.success(status, data);

        //then
        assertEquals(status, result.getStatus());
        assertEquals(data, result.getResponse());
        assertNull(result.getError());
    }

    @Test
    void fail_1() {
        //given
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "illegal argument";

        //when
        ApiUtil.ApiResult<?> result = fail(status, message);

        //then
        assertEquals(status, result.getStatus());
        assertNull(result.getResponse());
        assertEquals(message, result.getError().getMessage());
    }

    @Test
    void fail_2() {
        //given
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable throwable = new IllegalArgumentException("illegal argument");

        //when
        ApiUtil.ApiResult<?> result = fail(status, throwable);

        //then
        assertEquals(status, result.getStatus());
        assertNull(result.getResponse());
        assertEquals(throwable.getMessage(), result.getError().getMessage());
    }
}