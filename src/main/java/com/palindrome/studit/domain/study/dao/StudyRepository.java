package com.palindrome.studit.domain.study.dao;

import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    Page<Study> findAllByIsPublicTrue(Pageable pageable);

    Page<Study> findAllByEnrollments_User_UserId(Pageable pageable, Long userId);
    List<Study> findAllByEnrollments_User_UserId(Long userId);

    Optional<Study> findByShareCode(String shareCode);
    @Query(value = "select distinct s from Study s join fetch s.enrollments where s.status = 'UPCOMING' and s.startAt <= ?1")
    List<Study> findAllPreparedAt(LocalDateTime dateTime);
}
