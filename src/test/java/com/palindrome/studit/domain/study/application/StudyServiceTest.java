package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.domain.OAuthProviderType;
import com.palindrome.studit.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({StudyService.class, AuthService.class})
class StudyServiceTest {
    @Autowired
    private StudyService studyService;

    @Autowired
    private AuthService authService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyEnrollmentRepository studyEnrollmentRepository;

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
}
