package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    Page<Study> findAllByIsPublicTrue(Pageable pageable);

    Page<Study> findAllByEnrollments_User_UserId(Pageable pageable, Long userId);

    Optional<Study> findByShareCode(String shareCode);
}
