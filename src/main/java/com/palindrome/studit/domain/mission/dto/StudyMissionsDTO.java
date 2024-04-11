package com.palindrome.studit.domain.mission.dto;

import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.mission.domain.MissionStatus;
import com.palindrome.studit.domain.study.dto.StudyDetailDTO;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
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

    public static Page<StudyDetailDTO.Mission> toDTO(Page<MissionState> missionStates) {
        List<StudyDetailDTO.Mission> missions = missionStates.stream()
                .flatMap(missionState -> {
                    Stream<StudyDetailDTO.Mission> completedMissions = missionState.getMissionLogs().stream()
                            .map(missionLog -> StudyDetailDTO.Mission.builder()
                                    .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                                    .startAt(missionState.getStartAt())
                                    .endAt(missionState.getEndAt())
                                    .status(MissionStatus.COMPLETED)
                                    .completedMissionUrl(missionLog.getCompletedMissionUrl())
                                    .build());

                    Stream<StudyDetailDTO.Mission> incompleteMissions = Stream.generate(() -> StudyDetailDTO.Mission.builder()
                                    .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                                    .startAt(missionState.getStartAt())
                                    .endAt(missionState.getEndAt())
                                    .status(MissionStatus.INCOMPLETE)
                                    .completedMissionUrl(null)
                                    .build())
                            .limit(missionState.getIncompleteMissionCounts());

                    return Stream.concat(completedMissions, incompleteMissions);
                })
                .collect(Collectors.toList());

        return missions;

    }
}
