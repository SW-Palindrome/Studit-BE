package com.palindrome.studit.domain.study.domain;

import com.palindrome.studit.domain.user.domain.User;
import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "study_id" }) })
public class StudyEnrollment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyEnrollmentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @Enumerated(EnumType.STRING)
    private StudyRole role;

    private String missionUrl;

    public void updateMissionUrl(String missionUrl) {
        this.missionUrl = missionUrl;
    }
}
