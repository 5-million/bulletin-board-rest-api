package xyz.fivemillion.bulletinboardapi.error;

public class DuplicateException extends CustomException {

    public DuplicateException(Error error) {
        super(error);
    }
}
