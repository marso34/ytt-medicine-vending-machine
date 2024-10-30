package com.example.ytt.domain.user.auth.jwt;

import com.example.ytt.domain.user.dto.Role;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JWTUtil {

    private SecretKey secretKey;

    @Value("${jwt.expiration.authorization}")
    private Long AUTHORIZATION_TOKEN_EXPIRATION;

    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRATION;
    public JWTUtil(@Value("${jwt.secret}")String secret){
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    public Long getAuthorizationTokenExpiration() {
        return AUTHORIZATION_TOKEN_EXPIRATION;
    }
    public Long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }

    // 토큰에서 추출
    
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // AuthorizationToken 생성
    public String createAuthorizationToken(String category, Long userId, String email, Role role) {
        return Jwts.builder()
                .claim("category", category)
                .claim("userId", userId)
                .claim("email",email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(String category, Long userId, String email, Role role) {
        return Jwts.builder()
                .claim("category", category)
                .claim("userId", userId)
                .claim("email",email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
}
