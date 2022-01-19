package xyz.fivemillion.bulletinboardapi.utils;

import xyz.fivemillion.bulletinboardapi.error.CustomException;
import xyz.fivemillion.bulletinboardapi.error.Error;

public class CheckUtil {

    public static void checkNotNull(Object obj, Class<? extends CustomException> exception, Error error) throws Exception {
        if (obj == null) {
            throwException(exception, error);
        }
    }

    public static void checkNotBlank(String obj, Class<? extends CustomException> exception, Error error) throws Exception {
        checkNotNull(obj, exception, error);
        if (obj.isBlank())
            throwException(exception, error);
    }

    private static void throwException(Class<? extends CustomException> exception, Error error) throws Exception {
        throw exception.getConstructor(Error.class).newInstance(error);
    }
}
