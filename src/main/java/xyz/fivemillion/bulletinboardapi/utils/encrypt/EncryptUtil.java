package xyz.fivemillion.bulletinboardapi.utils.encrypt;

public interface EncryptUtil {

    String encrypt(String password);
    boolean isMatch(String plainText, String hashed);
}
