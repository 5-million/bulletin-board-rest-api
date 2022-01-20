package xyz.fivemillion.bulletinboardapi.utils;

import org.springframework.test.web.servlet.ResultActions;
import xyz.fivemillion.bulletinboardapi.error.CustomException;
import xyz.fivemillion.bulletinboardapi.error.Error;

public class ResultActionsUtil {

    public static Exception getException(ResultActions ra) {
        return ra.andReturn().getResolvedException();
    }

    public static Error getError(ResultActions ra) {
        return ((CustomException) getException(ra)).getError();
    }
}
