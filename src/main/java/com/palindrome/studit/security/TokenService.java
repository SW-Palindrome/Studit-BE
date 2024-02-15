package com.palindrome.studit.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;

public class TokenService {

    private static final String SECRET = "temporarilySecret";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final int ACCESS_TOKEN_EXPIRE_PERIOD = 15 * 60 * 1000;  // 15분
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final int REFRESH_TOKEN_EXPIRE_PERIOD = 7 * 24 * 60 * 60 * 1000;  // 7일
    private static final String ID_CLAIM = "id";

    public static String createAccessToken(String id) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_PERIOD))
                .withClaim(ID_CLAIM, id)
                .sign(algorithm);
    }

    public static String createRefreshToken() {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC512(SECRET);
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_PERIOD))
                .sign(algorithm);
    }

    public static boolean validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(SECRET)).build().verify(token) != null;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
