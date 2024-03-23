package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.mission.domain.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {
    Optional<StudyLog> findByCompletedMissionUrl(String missionUrl);
}
