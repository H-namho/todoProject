package com.example.memorypractice.shared.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        this.secretKey= Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String createToken(Long userId, String username, String role, String tokenType, long expiration){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim("token_type", tokenType)
                .claim("issued_at_ms", now.getTime())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String createAcToken(Long userId, String username,String role){
        return createToken(userId, username, role, ACCESS_TOKEN_TYPE, accessTokenExpiration);
    }
    public String createReToekn(Long userId, String username,String role){
        return createToken(userId, username, role, REFRESH_TOKEN_TYPE, refreshTokenExpiration);
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token){
       return Long.parseLong(getClaims(token).getSubject());
    }

    public String getUsername(String token){
        return getClaims(token).get("username", String.class);
    }

    public String getRole(String token){
        return getClaims(token).get("role", String.class);
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_TYPE.equals(getClaims(token).get("token_type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(getClaims(token).get("token_type", String.class));
    }

    public long getIssuedAt(String token) {
        return getClaims(token).get("issued_at_ms", Long.class);
    }

    public long getRefreshTokenExpiration(){
        return refreshTokenExpiration/1000;
    }

    // 남은 만료시간 계산
    public long getRemainingExpiration(String token) {
        long expirationTime = getClaims(token).getExpiration().getTime();
        long currentTime = System.currentTimeMillis();
        return Math.max(expirationTime - currentTime, 0);
    }

    public boolean vaildateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
