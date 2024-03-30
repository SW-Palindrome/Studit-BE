package com.palindrome.studit.domain.mission.dao;

import com.palindrome.studit.domain.mission.domain.MissionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionLogRepository extends JpaRepository<MissionLog, Long> {
    Optional<MissionLog> findByCompletedMissionUrl(String missionUrl);
}
