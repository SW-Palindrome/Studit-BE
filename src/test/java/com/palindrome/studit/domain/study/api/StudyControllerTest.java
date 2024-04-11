package com.palindrome.studit.domain.study.api;

import com.palindrome.studit.domain.mission.application.MissionService;
import com.palindrome.studit.domain.study.application.StudyService;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.domain.OAuthProviderType;
import com.palindrome.studit.domain.user.domain.User;
import com.palindrome.studit.global.config.security.application.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Autowired
    private MissionService missionService;

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
                .andExpect(jsonPath("$.total_elements").value(15));

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
                .andExpect(jsonPath("$.total_elements").value(20));

    }

    @Test
    @DisplayName("미션 수행할 주소 수정 성공 테스트")
    void updateMissionUrlTest() throws Exception {
        //Given
        User user = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId");
        createStudies(user, true, 1);
        String accessToken = tokenService.createAccessToken(user.getUserId().toString());

        //When
        String requestJson = "{\"mission_url\": \"https://github.com/palindrome\"}";
        ResultActions mockResult = mockMvc.perform(patch("/studies/1/missions/url")
                .header("Authorization", "Bearer " + accessToken)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        mockResult.andExpect(status().isOk());
    }

    @Test
    @DisplayName("미션 수행할 주소 수정 실패 테스트")
    void updateMissionUrlFailureTest() throws Exception {
        //Given
        User user = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId");
        createStudies(user, true, 1);
        String accessToken = tokenService.createAccessToken(user.getUserId().toString());

        //When
        String requestJson = "{\"mission_url\": \"https://velog.io/@palindrome\"}";
        ResultActions mockResult = mockMvc.perform(patch("/studies/1/missions/url")
                .header("Authorization", "Bearer " + accessToken)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        mockResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 주소입니다."));
    }

    @Test
    @DisplayName("스터디 상세 정보 조회 테스트")
    void getStudyDetailsTest() throws Exception{
        // Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(10))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(false)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(leader.getUserId(), createStudyDTO);
        String accessToken = tokenService.createAccessToken(leader.getUserId().toString());

        // When
        ResultActions mockResult = mockMvc.perform(get("/studies/{studyId}/details",study.getStudyId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // When
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("신규 스터디"));
    }
}