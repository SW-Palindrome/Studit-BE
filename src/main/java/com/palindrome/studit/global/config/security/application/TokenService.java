package com.palindrome.studit.global.config.security.application;

import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenService {
    private SecretKey SECRET;
    @Value("${jwt.access-token.expire-period}")
    private int ACCESS_TOKEN_EXPIRE_PERIOD;
    @Value("${jwt.refresh-token.expire-period}")
    private int REFRESH_TOKEN_EXPIRE_PERIOD;

    @Autowired
    public TokenService(@Value("${jwt.secret}") String SECRET_KEY) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.SECRET = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String sub) {
        Date now = new Date();
        return Jwts.builder()
                .subject(sub)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_PERIOD))
                .signWith(SECRET)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_PERIOD))
                .signWith(SECRET)
                .compact();
    }

    public String parseSubject(String token) throws InvalidTokenException {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    public Date parseExpiration(String token) throws InvalidTokenException {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }
}
