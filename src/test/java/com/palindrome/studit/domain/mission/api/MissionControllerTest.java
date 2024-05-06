package com.palindrome.studit.domain.mission.api;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class MissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyService studyService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private TokenService tokenService;

    @Transactional
    @Test
    @DisplayName("내 금주 미션 상태 조회 테스트")
    void listMyWeeklyMissionStatesTest() throws Exception {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(10))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(true)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(leader.getUserId(), createStudyDTO);
        StudyEnrollment studyEnrollment = studyService.enroll(member.getUserId(), study.getStudyId());
        studyService.start(leader.getUserId(), study.getStudyId());
        missionService.submitMission(studyEnrollment, "https://completed1.com", LocalDateTime.now().plusDays(1));
        missionService.submitMission(studyEnrollment, "https://completed2.com", LocalDateTime.now().plusDays(2));
        String accessToken = tokenService.createAccessToken(member.getUserId().toString());

        //When
        ResultActions mockResult = mockMvc.perform(get("/missions/my/weekly")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_elements").value(1));
    }

    @Transactional
    @Test
    @DisplayName("스터디 활동 조회 테스트")
    void listActivitiesTest() throws Exception {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        User member2 = authService.createUser("member2@email.com", OAuthProviderType.GITHUB, "providerId3");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(10))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(true)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(leader.getUserId(), createStudyDTO);
        StudyEnrollment studyEnrollment = studyService.enroll(member.getUserId(), study.getStudyId());
        StudyEnrollment studyEnrollment2 = studyService.enroll(member2.getUserId(), study.getStudyId());
        studyService.start(leader.getUserId(), study.getStudyId());
        missionService.submitMission(studyEnrollment, "https://completed1.com", LocalDateTime.now().plusDays(1));
        missionService.submitMission(studyEnrollment, "https://completed2.com", LocalDateTime.now().plusDays(2));
        missionService.submitMission(studyEnrollment2, "https://completed3.com", LocalDateTime.now().plusDays(1));
        String accessToken = tokenService.createAccessToken(member.getUserId().toString());

        //When
        ResultActions mockResult = mockMvc.perform(get("/missions/my/activities")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        mockResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_elements").value(3));
    }
}
