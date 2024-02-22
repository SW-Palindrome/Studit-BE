package com.palindrome.studit.global.config.security.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.access-token.subject}")
    private String ACCESS_TOKEN_SUBJECT;
    @Value("${jwt.refresh-token.subject}")
    private String REFRESH_TOKEN_SUBJECT;
    @Value("${jwt.access-token.expire-period}")
    private int ACCESS_TOKEN_EXPIRE_PERIOD;
    @Value("${jwt.refresh-token.expire-period}")
    private int REFRESH_TOKEN_EXPIRE_PERIOD;
    private static final String ID_CLAIM = "id";

    public String createAccessToken(String id) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_PERIOD))
                .withClaim(ID_CLAIM, id)
                .sign(algorithm);
    }

    public String createRefreshToken() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_PERIOD))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(SECRET)).build().verify(token) != null;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
