package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.study.domain.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {
}
