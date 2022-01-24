package xyz.fivemillion.bulletinboardapi.utils;

import xyz.fivemillion.bulletinboardapi.error.BlankException;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.NullException;

public class CheckUtil {

    /**
     * @throws NullException If obj is null, a NullException(extends CustomException) is thrown.
     */
    public static void checkNotNull(Object obj, Error error) {
        if (obj == null)
            throw new NullException(error);
    }

    /**
     * @throws NullException If str is null, a NullException(extends CustomException) is thrown.
     * @throws BlankException if str is blank, a BlankException(extends CustomException) is thrown.
     */
    public static void checkNotBlank(String str, Error error) {
        checkNotNull(str, error);
        if (str.isBlank())
            throw new BlankException(error);
    }
}
