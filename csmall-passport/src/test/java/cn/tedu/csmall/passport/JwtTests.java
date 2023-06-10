package cn.tedu.csmall.passport;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTests {
    String secretKey = "jsd2302";

    @Test
    void generate() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 9527);
        claims.put("name", "张三");
        String jwt = Jwts.builder()
                // Header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // Payload
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000))
                // Verify Signature
                .signWith(SignatureAlgorithm.HS256, secretKey)
                // 生成
                .compact();

        System.out.println(jwt);
    }

    @Test
    void parse() {
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwIjoxNjg4MTMwNzI0LCJ1c2VybmFtZSI6InFpbGVpIn0.ZhOGT8NlYkK6VNCXDA93bg2r-3k-u97_N9Y46BNFO_E";
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        Long id = claims.get("id", Long.class);
        String name = claims.get("username", String.class);

        System.out.println("id = " + id);
        System.out.println("username = " + name);
    }
}
