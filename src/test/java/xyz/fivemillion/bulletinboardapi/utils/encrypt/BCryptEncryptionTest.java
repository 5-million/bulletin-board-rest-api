package xyz.fivemillion.bulletinboardapi.utils.encrypt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptEncryptionTest {

    private final BCryptEncryption bCryptEncryption = new BCryptEncryption();

    @Test
    void match_fail() {
        //given
        String password1 = "password1";
        String password2 = "password2";
        String hashed = bCryptEncryption.encrypt(password1);

        //when
        boolean result = bCryptEncryption.isMatch(password2, hashed);

        //then
        assertFalse(result);
    }

    @Test
    void match_success() {
        //given
        String plainText = "password";
        String hashed = bCryptEncryption.encrypt(plainText);

        //when
        boolean result = bCryptEncryption.isMatch(plainText, hashed);

        //then
        assertTrue(result);
    }

}