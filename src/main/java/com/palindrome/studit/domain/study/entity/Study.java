package com.palindrome.studit.domain.study.entity;

import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Study extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Size(max = 30)
    private String name;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Long maxMembers;

    @Size(max = 255)
    private String studyImage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StudyPurpose purpose;

    private String description;

    @NotNull
    private Boolean isPublic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StudyMission mission;

    private Integer missionCountPerWeek;

    @NotNull
    private Integer finePerMission;

    @Size(max = 50)
    private String shareCode;
}
