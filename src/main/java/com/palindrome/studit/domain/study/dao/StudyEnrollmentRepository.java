package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyEnrollmentRepository extends JpaRepository<StudyEnrollment, Long> {
}
