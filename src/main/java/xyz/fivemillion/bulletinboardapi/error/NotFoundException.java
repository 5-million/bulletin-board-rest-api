package xyz.fivemillion.bulletinboardapi.error;

public class NotFoundException extends CustomException {

    public NotFoundException(Error error) {
        super(error);
    }
}
