package com.palindrome.studit.domain.mission.dao;

import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface MissionLogRepository extends JpaRepository<MissionLog, Long> {
    Optional<MissionLog> findByCompletedMissionUrl(String missionUrl);
    Page<MissionLog> findAllByMissionState_StudyEnrollment_StudyIn(Pageable page, Collection<Study> studies);
}
