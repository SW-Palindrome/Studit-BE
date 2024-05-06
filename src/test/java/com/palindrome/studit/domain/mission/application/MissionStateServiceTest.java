package com.palindrome.studit.domain.mission.application;

import com.palindrome.studit.domain.mission.dao.MissionLogRepository;
import com.palindrome.studit.domain.mission.dao.MissionStateRepository;
import com.palindrome.studit.domain.mission.domain.MissionState;
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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({StudyService.class, AuthService.class, TokenService.class, MissionService.class})
class MissionStateServiceTest {
    @Autowired
    private MissionService missionService;

    @Autowired
    private MissionStateRepository missionStateRepository;

    @Autowired
    private MissionLogRepository missionLogRepository;

    @Autowired
    private StudyService studyService;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("미션 상태 생성 테스트")
    void createMissionStatesTest() {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(28))
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

        //When
        missionService.createMissionStates(studyEnrollment);

        //Then
        assertThat(missionStateRepository.count()).isEqualTo(4);
    }

    @Test
    @DisplayName("미션 제출 테스트")
    void submitMissionTest() {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
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
        List<MissionState> missionStates = missionService.createMissionStates(studyEnrollment);
        MissionState missionState = missionStates.get(0);

        //When
        missionService.submitMission(studyEnrollment, "https://completed-mission.com", LocalDateTime.now());

        //Then
        assertThat(missionLogRepository.count()).isEqualTo(1);
        assertThat(missionState.getIncompleteMissionCounts()).isEqualTo(2);
    }

    @Test
    @DisplayName("잘못된 기간의 미션 제출 테스트")
    void submitWrongPeriodMissionTest() {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
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
        missionService.createMissionStates(studyEnrollment);

        //When, Then
        assertThrows(NoSuchElementException.class, () -> missionService.submitMission(studyEnrollment, "https://completed-mission.com", LocalDateTime.now().plusDays(10)));
    }

    @Test
    @DisplayName("미션 수행 내역 조회 테스트")
    void listStudyMissionsTest() {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(28))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(true)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(leader.getUserId(), createStudyDTO);
        studyService.enroll(member.getUserId(), study.getStudyId());
        studyService.start(leader.getUserId(), study.getStudyId());

        //When
        Page<MissionState> missions = missionService.listStudyMissions(0, member.getUserId(), study.getStudyId());

        //Then
        assertThat(missions.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("미션 수행 내역 조회 실패 테스트")
    void listStudyMissionsFailureTest() {
        //Given
        User leader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(28))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(true)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(leader.getUserId(), createStudyDTO);
        studyService.start(leader.getUserId(), study.getStudyId());

        //When, Then
        assertThrows(EntityNotFoundException.class, () -> {
            missionService.listStudyMissions(0, member.getUserId(), study.getStudyId());
        });
    }
}
