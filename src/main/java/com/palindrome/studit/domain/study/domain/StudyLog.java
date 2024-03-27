package com.palindrome.studit.domain.study.domain;

import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyLogId;

    @ManyToOne
    @JoinColumn(name = "study_enrollment_id")
    private StudyEnrollment studyEnrollment;

    @Column(unique = true)
    private String completedMissionUrl;

    private LocalDateTime completedAt;
}
