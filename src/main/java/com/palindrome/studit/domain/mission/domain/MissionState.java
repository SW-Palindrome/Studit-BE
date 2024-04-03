package com.palindrome.studit.domain.mission.domain;

import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MissionState extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionStateId;

    @ManyToOne
    @JoinColumn(name = "study_enrollment_id")
    private StudyEnrollment studyEnrollment;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Integer uncompletedMissionCounts;

    @OneToMany(mappedBy = "missionState")
    private List<MissionLog> missionLogs = new ArrayList<>();

    public void submitMission() {
        this.uncompletedMissionCounts = Math.max(this.uncompletedMissionCounts - 1, 0);
    }
}
