package com.palindrome.studit.domain.study.dto;

import com.palindrome.studit.domain.study.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StudyResponseDTO {
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long maxMembers;
    private String studyImage;
    private StudyPurpose purpose;
    private String description;
    private Boolean isPublic;
    private StudyStatus status;
    private MissionType missionType;
    private Integer missionCountPerWeek;
    private Integer finePerMission;

    public static Page<StudyResponseDTO> toDTOPage(Page<Study> studies) {
        return studies.map(study -> StudyResponseDTO.builder()
                .name(study.getName())
                .startAt(study.getStartAt())
                .endAt(study.getEndAt())
                .maxMembers(study.getMaxMembers())
                .studyImage(study.getStudyImage())
                .purpose(study.getPurpose())
                .description(study.getDescription())
                .isPublic(study.getIsPublic())
                .status(study.getStatus())
                .missionType(study.getMission().getMissionType())
                .missionCountPerWeek(study.getMission().getMissionCountPerWeek())
                .finePerMission(study.getMission().getFinePerMission())
                .build());
    }
}
