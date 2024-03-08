package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.*;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return studyRepository.findAll(pageable);
    }
}
