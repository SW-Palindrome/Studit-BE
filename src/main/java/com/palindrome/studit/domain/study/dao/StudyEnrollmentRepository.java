package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyEnrollmentRepository extends JpaRepository<StudyEnrollment, Long> {
    Optional<StudyEnrollment> findByUserAndStudy(User user, Study study);
}
