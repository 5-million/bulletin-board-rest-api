package xyz.fivemillion.bulletinboardapi.error;

public class UnAuthorizedException extends CustomException {

    public UnAuthorizedException(Error error) {
        super(error);
    }
}
