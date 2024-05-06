package com.palindrome.studit.domain.study.dto;

import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class StudyDetailDTO {
        private Long studyId;
        private String name;
        private String description;
        private StudyPurpose purpose;
        private MissionType missionType;
        private Integer missionCountPerWeek;
        private Integer finePerMission;
        private String studyImage;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private boolean isPublic;
}
