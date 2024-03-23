package com.palindrome.studit.domain.study.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StudyMissionInfo {
    @NotNull
    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    private Integer missionCountPerWeek;

    @NotNull
    private Integer finePerMission;
}
