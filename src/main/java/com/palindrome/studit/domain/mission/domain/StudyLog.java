package com.palindrome.studit.domain.mission.domain;

import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StudyLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyLogId;

    @ManyToOne
    @JoinColumn(name = "mission_state_id")
    private MissionState missionState;

    @Column(unique = true)
    private String completedMissionUrl;

    private LocalDateTime completedAt;
}
