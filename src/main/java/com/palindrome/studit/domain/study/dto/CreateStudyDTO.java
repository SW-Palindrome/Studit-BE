package com.palindrome.studit.domain.study.dto;

import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class CreateStudyDTO {
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long maxMembers;
    private StudyPurpose purpose;
    private String description;
    private Boolean isPublic;
    private MissionType missionType;
    private Integer missionCountPerWeek;
    private Integer finePerMission;
}
