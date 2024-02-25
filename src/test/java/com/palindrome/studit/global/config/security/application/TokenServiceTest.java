package com.palindrome.studit.global.config.security.application;

import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("엑세스 토큰 생성 테스트")
    void createAccessTokenTest() {
        //Given
        String sub = "testSub";

        //When
        String accessToken = tokenService.createAccessToken(sub);

        //Then
        assertThat(accessToken).isNotBlank();
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createRefreshTokenTest() {
        //When
        String accessToken = tokenService.createRefreshToken();

        //Then
        assertThat(accessToken).isNotBlank();
    }

    @Test
    @DisplayName("엑세스 토큰 생성 후 sub 추출 테스트")
    void parseAccessTokenSubjectTest() throws InvalidTokenException {
        //Given
        String sub = "testSub";
        String accessToken = tokenService.createAccessToken(sub);

        //When
        String parsedSub = tokenService.parseSubject(accessToken);

        //Then
        assertThat(parsedSub).isEqualTo(sub);
    }

    @Test
    @DisplayName("엑세스 토큰 생성 후 sub 추출 테스트")
    void parseAccessTokenExpirationTest() throws InvalidTokenException {
        //Given
        String sub = "testSub";
        String accessToken = tokenService.createAccessToken(sub);

        //When
        Date parsedExpiration = tokenService.parseExpiration(accessToken);

        //Then
        assertThat(parsedExpiration).isNotNull();
    }

    @Test
    @DisplayName("잘못된 엑세스 토큰 검증 테스트")
    void accessTokenValidationErrorTest() {
        //Given
        String sub = "testSub";
        String accessToken = tokenService.createAccessToken(sub) + '0';

        //When, Then
        assertThrows(InvalidTokenException.class, () -> {
            tokenService.parseSubject(accessToken);
        });
    }

}