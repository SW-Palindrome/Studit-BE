package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.mission.application.MissionService;
import com.palindrome.studit.domain.mission.dao.MissionStateRepository;
import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.*;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.study.dto.MissionUrlRequestDTO;
import com.palindrome.studit.domain.study.dto.StudyDetailDTO;
import com.palindrome.studit.domain.study.exception.DuplicatedStudyEnrollmentException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({StudyService.class, AuthService.class, TokenService.class, MissionService.class})
class StudyServiceTest {
    @Autowired
    private StudyService studyService;

    @Autowired
    private AuthService authService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyEnrollmentRepository studyEnrollmentRepository;

    @Autowired
    private MissionStateRepository missionStateRepository;

    @Test
    @DisplayName("스터디 그룹 생성 테스트")
    void createStudyTest() {
        //Given
        User user = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId");
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

        //When
        Study study = studyService.createStudy(user.getUserId(), createStudyDTO);

        //Then
        assertThat(study).isNotNull();
        assertThat(studyRepository.count()).isEqualTo(1);
        assertThat(studyEnrollmentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 시작 테스트")
    void startTest() {
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

        //When
        studyService.start(leader.getUserId(), study.getStudyId());

        //Then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.IN_PROGRESS);
        assertThat(missionStateRepository.findAllByStudyEnrollment_User_UserId(leader.getUserId())).isNotEmpty();
        assertThat(missionStateRepository.findAllByStudyEnrollment_User_UserId(member.getUserId())).isNotEmpty();
    }

    @Test
    @DisplayName("허용되지 않은 멤버의 스터디 시작 테스트")
    void startByMemberFailedTest() {
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

        //When, Then
        assertThrows(AccessDeniedException.class, () -> studyService.start(member.getUserId(), study.getStudyId()));
    }

    @Test
    @DisplayName("스터디 리스트 조회 테스트")
    void getListTest() {
        //Given
        User user = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId");
        for (int idx = 0; idx < 3; idx++) {
            CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                    .name("신규 스터디 " + idx)
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

            studyService.createStudy(user.getUserId(), createStudyDTO);
        }

        for (int idx = 0; idx < 3; idx++) {
            CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                    .name("신규 스터디 " + idx)
                    .startAt(LocalDateTime.now())
                    .endAt(LocalDateTime.now().plusDays(7))
                    .maxMembers(10L)
                    .purpose(StudyPurpose.ALGORITHM)
                    .description("테스트용 스터디입니다.")
                    .isPublic(false)
                    .missionType(MissionType.GITHUB)
                    .missionCountPerWeek(3)
                    .finePerMission(100_000)
                    .build();

            studyService.createStudy(user.getUserId(), createStudyDTO);
        }

        //When
        Page<Study> studies = studyService.getList(0);

        //Then
        assertThat(studies.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("스터디 참여 성공 테스트")
    void enrollTest() {
        //Given
        User studyLeader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
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
        Study study = studyService.createStudy(studyLeader.getUserId(), createStudyDTO);

        //When
        StudyEnrollment studyEnrollment = studyService.enroll(member.getUserId(), study.getStudyId());

        //Then
        assertThat(studyEnrollment).isNotNull();
        assertThat(studyEnrollmentRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("비공개 스터디 참여 실패 테스트")
    void enrollNotPublicStudyTest() {
        //Given
        User studyLeader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(false)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(studyLeader.getUserId(), createStudyDTO);

        //When, Then
        assertThrows(AccessDeniedException.class, () -> studyService.enroll(member.getUserId(), study.getStudyId()));
    }

    @Test
    @DisplayName("스터디 참여 중복 요청 실패 테스트")
    void duplicatedEnrollTest() {
        //Given
        User studyLeader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
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
        Study study = studyService.createStudy(studyLeader.getUserId(), createStudyDTO);
        studyService.enroll(member.getUserId(), study.getStudyId());

        //When, Then
        assertThrows(DuplicatedStudyEnrollmentException.class, () -> studyService.enroll(member.getUserId(), study.getStudyId()));
    }

    @Test
    @DisplayName("공유 코드로 스터디 참여 테스트")
    void enrollWithShareCodeTest() {
        //Given
        User studyLeader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User member = authService.createUser("member@email.com", OAuthProviderType.GITHUB, "providerId2");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(false)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(studyLeader.getUserId(), createStudyDTO);

        //When
        StudyEnrollment studyEnrollment = studyService.enrollWithShareCode(member.getUserId(), study.getShareCode());

        //Then
        assertThat(studyEnrollment).isNotNull();
        assertThat(studyEnrollmentRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("미션 주소 수정 성공 테스트")
    void updateMissionUrlTest() {
        //Given
        User user = authService.createUser("user@email.com", OAuthProviderType.GITHUB, "providerId1");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(false)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(user.getUserId(), createStudyDTO);
        MissionUrlRequestDTO missionUrlRequestDTO = MissionUrlRequestDTO.builder()
                .missionUrl("https://github.com/username").build();

        //When
        studyService.updateMissionUrl(user.getUserId(), study.getStudyId(), missionUrlRequestDTO);

        //Then
        Optional<StudyEnrollment> optionalStudyEnrollment = studyEnrollmentRepository.findByUserAndStudy(user, study);
        StudyEnrollment studyEnrollment = optionalStudyEnrollment.get();
        assertThat(studyEnrollment.getMissionUrl().equals(missionUrlRequestDTO.getMissionUrl()));
    }

    @Test
    @DisplayName("존재하지 않는 스터디에 대한 미션 주소 수정 실패 테스트")
    void updateMissionUrlForNonExistingStudyTest() {
        User user = authService.createUser("user@email.com", OAuthProviderType.GITHUB, "providerId1");
        Long nonExistingStudyId = 1000L;
        MissionUrlRequestDTO missionUrlRequestDTO = MissionUrlRequestDTO.builder()
                .missionUrl("https://github.com/username").build();

        //When, Then
        assertThrows(EntityNotFoundException.class, () -> {studyService.updateMissionUrl(user.getUserId(), nonExistingStudyId, missionUrlRequestDTO);});
    }

    @Test
    @DisplayName("참여 내역 없는 스터디 미션 수정 실패 테스트")
    void updateMissionUrlNonExistingStudyEnrollmentTest() {
        //Given
        User studyLeader = authService.createUser("test@email.com", OAuthProviderType.GITHUB, "providerId1");
        User user = authService.createUser("user@email.com", OAuthProviderType.GITHUB, "providerId1");
        CreateStudyDTO createStudyDTO = CreateStudyDTO.builder()
                .name("신규 스터디")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(7))
                .maxMembers(10L)
                .purpose(StudyPurpose.ALGORITHM)
                .description("테스트용 스터디입니다.")
                .isPublic(false)
                .missionType(MissionType.GITHUB)
                .missionCountPerWeek(3)
                .finePerMission(100_000)
                .build();
        Study study = studyService.createStudy(studyLeader.getUserId(), createStudyDTO);
        MissionUrlRequestDTO missionUrlRequestDTO = MissionUrlRequestDTO.builder()
                .missionUrl("https://github.com/username").build();

        //When, Then
        assertThrows(EntityNotFoundException.class, () -> {
            studyService.updateMissionUrl(user.getUserId(), study.getStudyId(), missionUrlRequestDTO);
        });
    }

    @Test
    @DisplayName("github 미션 주소 검증 테스트")
    void validateGithubMissionUrlTest() {
        //Given
        MissionType missionTypeGithub = MissionType.GITHUB;
        String githubTestUrl = "https://github.com/username";

        //When
        String githubMissionUrl = studyService.validateMissionUrl(missionTypeGithub, githubTestUrl);

        //Then
        assertEquals(githubTestUrl, githubMissionUrl);
    }

    @Test
    @DisplayName("velog 미션 주소 검증 테스트")
    void validateVelogMissionUrlTest() {
        //Given
        MissionType missionTypeVelog = MissionType.VELOG;
        String velogTestUrl = "https://velog.io/@username";

        //When
        String velogMissionUrl = studyService.validateMissionUrl(missionTypeVelog, velogTestUrl);

        //Then
        assertEquals(velogTestUrl, velogMissionUrl);
    }

    @Test
    @DisplayName("velog 미션 주소 검증 실패 테스트")
    void validateVelogMissionUrlFailureTest() {
        //Given
        MissionType missionTypeVelog = MissionType.VELOG;
        String velogTestUrl = "https://velog.io/username";
        String githubTestUrl = "https://github.com/username";

        //When, Then
        assertThrows(IllegalArgumentException.class, () -> {
            studyService.validateMissionUrl(missionTypeVelog, githubTestUrl);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            studyService.validateMissionUrl(missionTypeVelog, velogTestUrl);
        });
    }

    @Test
    @DisplayName("github 미션 주소 검증 실패 테스트")
    void validateGithubMissionUrlFailureTest() {
        // Given
        MissionType missionTypeGithub = MissionType.GITHUB;
        String githubTestUrl = "https://github.com/@username";
        String velogTestUrl = "https://velog.io/@username";

        // When, Then
        assertThrows(IllegalArgumentException.class, () -> {
            studyService.validateMissionUrl(missionTypeGithub, velogTestUrl);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            studyService.validateMissionUrl(missionTypeGithub, githubTestUrl);
        });
    }

    @Test
    @DisplayName("스터디 상세정보 조회 성공 테스트")
    void getStudyDetailsTest() {
        //Given
        User leader = authService.createUser("user@email.com", OAuthProviderType.GITHUB, "providerId1");
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
        studyService.enroll(member.getUserId(), study.getStudyId());

        //When
        StudyDetailDTO studyDetailDTO = studyService.getStudyDetails(member.getUserId(), study.getStudyId());

        //Then
        assertThat(studyDetailDTO).isNotNull();
        assertThat(studyDetailDTO.getName()).isNotNull();
    }
}
