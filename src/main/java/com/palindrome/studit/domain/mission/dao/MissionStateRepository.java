package com.palindrome.studit.domain.mission.dao;

import com.palindrome.studit.domain.mission.domain.MissionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionStateRepository extends JpaRepository<MissionState, Long> {
}
