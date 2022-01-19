package xyz.fivemillion.bulletinboardapi.error;

public class IllegalArgumentException extends CustomException {

    public IllegalArgumentException(Error error) {
        super(error);
    }
}
