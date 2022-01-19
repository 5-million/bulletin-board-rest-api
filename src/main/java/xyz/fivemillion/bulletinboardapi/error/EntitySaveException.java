package xyz.fivemillion.bulletinboardapi.error;

public class EntitySaveException extends CustomException {

    public EntitySaveException(Error error) {
        super(error);
    }
}
