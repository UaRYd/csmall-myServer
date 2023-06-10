package cn.tedu.csmall.passport;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptTests {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void xxx() {
        String rawPassword = "123456";
        System.out.println("原文：" + rawPassword);

        for (int i = 0; i < 500; i++) {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println(encodedPassword);
            boolean result = passwordEncoder.matches(rawPassword, encodedPassword);
            System.out.println(result);
        }
    }
}
