package com.palindrome.studit.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;

public class TokenService {

    private static final String SECRET = "temporarilySecret";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String EMAIL_CLAIM = "email";
    private static final int ACCESS_TOKEN_EXPIRE_PERIOD = 15 * 60 * 1000;  // 15분

    public String createAccessToken(String email) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_PERIOD))
                .withClaim(EMAIL_CLAIM, email)
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
