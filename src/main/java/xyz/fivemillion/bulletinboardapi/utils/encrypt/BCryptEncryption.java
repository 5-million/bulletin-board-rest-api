package xyz.fivemillion.bulletinboardapi.utils.encrypt;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptEncryption implements EncryptUtil {

    @Override
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean isMatch(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}
