package com.palindrome.studit.domain.mission.dao;

import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionStateRepository extends JpaRepository<MissionState, Long> {
    List<MissionState> findAllByStudyEnrollment_User_UserId(Long userId);

    Optional<MissionState> findByStudyEnrollment_StudyEnrollmentIdAndStartAtBeforeAndEndAtAfter(Long studyEnrollmentId, LocalDateTime startAt, LocalDateTime endAt);
    Optional<MissionState> findByStudyEnrollmentAndStartAtLessThanEqualAndEndAtGreaterThanEqual(StudyEnrollment studyEnrollment, LocalDateTime startAt, LocalDateTime endAt);
}
