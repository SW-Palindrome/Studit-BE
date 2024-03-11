package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.*;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.study.exception.DuplicatedStudyEnrollmentException;
import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyEnrollmentRepository studyEnrollmentRepository;

    @Transactional
    public Study createStudy(Long userId, CreateStudyDTO createStudyDTO) {
        User user = userRepository.getReferenceById(userId);

        Mission mission = Mission.builder()
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
                .shareCode(UUID.randomUUID().toString())
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

    public Page<Study> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.DEFAULT_DIRECTION, "studyId");
        return studyRepository.findAllByIsPublicTrue(pageable);
    }

    public Page<Study> getListByUser(int page, Long userId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.DEFAULT_DIRECTION, "studyId");
        return studyRepository.findAllByEnrollments_User_UserId(pageable, userId);
    }

    public StudyEnrollment enroll(Long userId, Long studyId) throws DuplicatedStudyEnrollmentException {
        User user = userRepository.getReferenceById(userId);
        Study study = studyRepository.getReferenceById(studyId);

        StudyEnrollment studyEnrollment = StudyEnrollment.builder()
                .user(user)
                .study(study)
                .role(StudyRole.MEMBER)
                .build();
        try {
            studyEnrollmentRepository.save(studyEnrollment);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedStudyEnrollmentException("중복된 스터디 등록 요청입니다.");
        }

        return studyEnrollment;
    }
}
