package xyz.fivemillion.bulletinboardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.BCryptEncryption;
import xyz.fivemillion.bulletinboardapi.utils.encrypt.EncryptUtil;

@Configuration
public class SpringConfig {

    @Bean
    public EncryptUtil encryptUtil() {
        return new BCryptEncryption();
    }
}
