package com.palindrome.studit.domain.study.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Mission {
    @NotNull
    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    private Integer missionCountPerWeek;

    @NotNull
    private Integer finePerMission;
}
