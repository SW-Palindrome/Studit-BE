package com.palindrome.studit.domain.study.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Builder
@RequiredArgsConstructor
public class Mission {
    @NotNull
    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    private Integer missionCountPerWeek;

    @NotNull
    private Integer finePerMission;
}
