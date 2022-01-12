package xyz.fivemillion.bulletinboardapi.error;

public class IllegalPasswordException extends CustomException {

    public IllegalPasswordException(Error error) {
        super(error);
    }
}
