package com.palindrome.studit.domain.study.api;

import com.palindrome.studit.domain.study.application.StudyService;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.domain.OAuthProviderType;
import com.palindrome.studit.domain.user.domain.User;
import com.palindrome.studit.global.config.security.application.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyService studyService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    private void createStudies(User user, boolean isPublic, int studyCount) {
        for (int idx = 0; idx < studyCount; idx++) {
            CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                    .name("신규 스터디 " + idx)
                    .startAt(LocalDateTime.now())
                    .endAt(LocalDateTime.now().plusDays(7))
                    .maxMembers(10L)
                    .purpose(StudyPurpose.ALGORITHM)
                    .description("테스트용 스터디입니다.")
                    .isPublic(isPublic)
                    .missionType(MissionType.GITHUB)
                    .missionCountPerWeek(3)
                    .finePerMission(100_000)
                    .build();

            studyService.createStudy(user.getUserId(), createStudyDTO);
        }
    }

    @Test
    @DisplayName("스터디 리스트 조회 테스트")
    void listStudiesTest() throws Exception {
        //Given
        User user = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId");
        createStudies(user, true, 15);

        //When
        ResultActions mockResult = mockMvc.perform(get("/studies").param("page", "0"));

        //Then
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(15));

    }

    @Test
    @DisplayName("내 스터디 리스트 조회 테스트")
    void listMyStudiesTest() throws Exception {
        //Given
        User user1 = authService.createUser("test1@email.com", OAuthProviderType.GITHUB, "providerId1");
        User user2 = authService.createUser("test2@email.com", OAuthProviderType.GITHUB, "providerId2");
        createStudies(user1, true, 10);
        createStudies(user1, false, 10);
        createStudies(user2, true, 10);
        String accessToken = tokenService.createAccessToken(user1.getUserId().toString());

        //When
        ResultActions mockResult = mockMvc.perform(get("/studies/me")
                .header("Authorization", "Bearer " + accessToken)
                .param("page", "0"));

        //Then
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(20));

    }
}