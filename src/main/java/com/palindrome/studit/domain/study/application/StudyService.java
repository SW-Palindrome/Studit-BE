package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.mission.application.MissionService;
import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.*;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.study.dto.MissionUrlRequestDTO;
import com.palindrome.studit.domain.study.dto.StudyDetailDTO;
import com.palindrome.studit.domain.study.exception.AlreadyStartedStudyException;
import com.palindrome.studit.domain.study.exception.DuplicatedStudyEnrollmentException;
import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyEnrollmentRepository studyEnrollmentRepository;
    private final MissionService missionStateService;

    @Transactional
    public Study createStudy(Long userId, CreateStudyDTO createStudyDTO) {
        User user = userRepository.getReferenceById(userId);

        MissionInfo mission = MissionInfo.builder()
                .missionType(createStudyDTO.getMissionType())
                .missionCountPerWeek(createStudyDTO.getMissionCountPerWeek())
                .finePerMission(createStudyDTO.getFinePerMission())
                .build();

        Study study = Study.builder()
                .name(createStudyDTO.getName())
                .startAt(createStudyDTO.getStartAt())
                .endAt(createStudyDTO.getEndAt())
                .maxMembers(createStudyDTO.getMaxMembers())
                .purpose(createStudyDTO.getPurpose())
                .description(createStudyDTO.getDescription())
                .isPublic(createStudyDTO.getIsPublic())
                .status(StudyStatus.UPCOMING)
                .mission(mission)
                .shareCode(RandomStringUtils.randomAlphanumeric(5))
                .build();

        studyRepository.save(study);

        StudyEnrollment studyEnrollment = StudyEnrollment.builder()
                .user(user)
                .study(study)
                .role(StudyRole.LEADER)
                .build();

        studyEnrollmentRepository.save(studyEnrollment);

        return study;
    }

    @Transactional
    public void start(Long userId, Long studyId) throws AlreadyStartedStudyException, AccessDeniedException {
        StudyEnrollment leaderStudyEnrollment = studyEnrollmentRepository.findByUser_UserIdAndStudy_StudyId(userId, studyId).orElseThrow();

        if (!leaderStudyEnrollment.getRole().equals(StudyRole.LEADER)) throw new AccessDeniedException("허가되지 않은 스터디입니다.");

        Study study = leaderStudyEnrollment.getStudy();

        study.start();

        for (StudyEnrollment studyEnrollment : studyEnrollmentRepository.findAllByStudy_StudyId(studyId)) {
            missionStateService.createMissionStates(studyEnrollment);
        }
    }

    public Page<Study> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.DEFAULT_DIRECTION, "studyId");
        return studyRepository.findAllByIsPublicTrue(pageable);
    }

    public Page<Study> getListByUser(int page, Long userId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.DEFAULT_DIRECTION, "studyId");
        return studyRepository.findAllByEnrollments_User_UserId(pageable, userId);
    }

    public StudyEnrollment enroll(Long userId, Long studyId) throws DuplicatedStudyEnrollmentException, AccessDeniedException {
        User user = userRepository.getReferenceById(userId);
        Study study = studyRepository.findById(studyId).orElseThrow();

        if (!study.getIsPublic()) {
            throw new AccessDeniedException("허가되지 않은 스터디 참여 요청입니다.");
        }

        StudyEnrollment studyEnrollment = StudyEnrollment.builder()
                .user(user)
                .study(study)
                .role(StudyRole.MEMBER)
                .build();
        try {
            studyEnrollmentRepository.save(studyEnrollment);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedStudyEnrollmentException("중복된 스터디 참여 요청입니다.");
        }

        return studyEnrollment;
    }

    public StudyEnrollment enrollWithShareCode(Long userId, String shareCode) throws DuplicatedStudyEnrollmentException {
        User user = userRepository.getReferenceById(userId);
        Study study = studyRepository.findByShareCode(shareCode).orElseThrow();

        StudyEnrollment studyEnrollment = StudyEnrollment.builder()
                .user(user)
                .study(study)
                .role(StudyRole.MEMBER)
                .build();
        try {
            studyEnrollmentRepository.save(studyEnrollment);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedStudyEnrollmentException("중복된 스터디 참여 요청입니다.");
        }

        return studyEnrollment;
    }

    @Transactional
    public void updateMissionUrl(Long userId, Long studyId, MissionUrlRequestDTO missionUrlRequestDTO) {
        User user = userRepository.getReferenceById(userId);
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디입니다."));
        String missionUrl = validateMissionUrl(study.getMission().getMissionType(), missionUrlRequestDTO.getMissionUrl());

        StudyEnrollment studyEnrollment = studyEnrollmentRepository.findByUserAndStudy(user, study).orElseThrow(() -> new EntityNotFoundException("해당 스터디 참여 내역이 없습니다."));
        studyEnrollment.updateMissionUrl(missionUrl);
    }

    public String validateMissionUrl(MissionType missionType, String missionUrl) {
        final Map<MissionType, Pattern> missionTypeUrlMap = Map.of(
                MissionType.GITHUB, Pattern.compile("https://github.com/\\w+"),
                MissionType.VELOG, Pattern.compile("https://velog.io/@\\w+")
        );

        Pattern pattern = missionTypeUrlMap.get(missionType);
        if (!pattern.matcher(missionUrl).matches()) {
            throw new IllegalArgumentException("잘못된 주소입니다.");
        }
        return missionUrl;
    }

    public StudyDetailDTO getStudyDetails(Long userId, Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디입니다."));
        if (!study.getIsPublic()) {
            studyEnrollmentRepository.findByUser_UserIdAndStudy_StudyId(userId, studyId).orElseThrow(() -> new EntityNotFoundException("사용자는 해당 스터디에 참여하고 있지 않습니다."));
        }
        return StudyDetailDTO.builder()
                .studyId(study.getStudyId())
                .name(study.getName())
                .description(study.getDescription())
                .purpose(study.getPurpose())
                .missionType(study.getMission().getMissionType())
                .missionCountPerWeek(study.getMission().getMissionCountPerWeek())
                .finePerMission(study.getMission().getFinePerMission())
                .studyImage(study.getStudyImage())
                .startAt(study.getStartAt())
                .endAt(study.getEndAt())
                .isPublic(study.getIsPublic()).build();
    }
}
