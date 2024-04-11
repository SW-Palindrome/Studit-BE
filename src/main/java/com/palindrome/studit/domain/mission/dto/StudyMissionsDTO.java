package com.palindrome.studit.domain.mission.dto;

import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.mission.domain.MissionStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Getter
public class StudyMissionsDTO {
    private String nickname;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private MissionStatus status;
    private String completedMissionUrl;

    public static List<StudyMissionsDTO> toDTO(Page<MissionState> missionStates) {
        List<StudyMissionsDTO> allMissions = missionStates.stream().flatMap(missionState -> {

            List<StudyMissionsDTO> completedMissions = missionState.getMissionLogs().stream()
                    .map(missionLog -> StudyMissionsDTO.builder()
                            .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                            .startAt(missionState.getStartAt())
                            .endAt(missionState.getEndAt())
                            .status(MissionStatus.COMPLETED)
                            .completedMissionUrl(missionLog.getCompletedMissionUrl())
                            .build())
                    .toList();

            List<StudyMissionsDTO> incompleteMissions = Stream.generate(() -> StudyMissionsDTO.builder()
                            .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                            .startAt(missionState.getStartAt())
                            .endAt(missionState.getEndAt())
                            .status(MissionStatus.INCOMPLETE)
                            .completedMissionUrl(null)
                            .build())
                    .limit(missionState.getIncompleteMissionCounts())
                    .toList();

            List<StudyMissionsDTO> missions = new ArrayList<>();
            missions.addAll(completedMissions);
            missions.addAll(incompleteMissions);
            return missions.stream();
        }).collect(Collectors.toList());

        return allMissions;
    }

    public static Page<StudyMissionsDTO> toDTOPage(Page<MissionState> missionStates) {
        return new PageImpl<>(StudyMissionsDTO.toDTO(missionStates), missionStates.getPageable(), missionStates.getTotalElements());
    }
}
