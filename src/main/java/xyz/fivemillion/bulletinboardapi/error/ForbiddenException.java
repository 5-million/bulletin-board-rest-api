package xyz.fivemillion.bulletinboardapi.error;

public class ForbiddenException extends CustomException {

    public ForbiddenException(Error error) {
        super(error);
    }
}
