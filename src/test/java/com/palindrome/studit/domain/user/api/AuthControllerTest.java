package com.palindrome.studit.domain.user.api;

import com.palindrome.studit.global.config.security.application.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("엑세스 토큰 검증 성공 테스트")
    void getJwtInfoTest() throws Exception {
        //Given
        String sub = "testSub";
        String accessToken = tokenService.createAccessToken(sub);

        //When
        ResultActions mockResult = mockMvc.perform(get("/auth/token-info/" + accessToken));

        //Then
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value(sub))
                .andExpect(jsonPath("$.exp").exists());
    }

    @Test
    @DisplayName("엑세스 토큰 검증 실패 테스트")
    void invalidAccessTokenRequestTest() throws Exception {
        //Given
        String sub = "testSub";
        String accessToken = tokenService.createAccessToken(sub) + "0";

        //When
        ResultActions mockResult = mockMvc.perform(get("/auth/token-info/" + accessToken));

        //Then
        mockResult.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("엑세스 토큰 시간 만료 테스트")
    void expiredAccessTokenRequestTest() throws Exception {
        //Given
        String sub = "testSub";
        Object originalTokenExpirePeriod = ReflectionTestUtils.getField(tokenService, "ACCESS_TOKEN_EXPIRE_PERIOD");
        ReflectionTestUtils.setField(tokenService, "ACCESS_TOKEN_EXPIRE_PERIOD", 1);
        String accessToken = tokenService.createAccessToken(sub);

        //모킹한 엑세스 토큰 만료 시간을 되돌린다.
        ReflectionTestUtils.setField(tokenService, "ACCESS_TOKEN_EXPIRE_PERIOD", originalTokenExpirePeriod);

        Thread.sleep(1);

        //When
        ResultActions mockResult = mockMvc.perform(get("/auth/token-info/{accessToken}", accessToken));

        //Then
        mockResult.andExpect(status().isBadRequest());
    }
}
