package com.palindrome.studit.domain.mission.dto;

import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyMissionStateResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    static class CompletedMissionInfo {
        private LocalDateTime completedAt;
        private String completedMissionUrl;

        public static List<CompletedMissionInfo> toDTO(List<MissionLog> missionLogs) {
            return missionLogs.stream().map(missionLog -> CompletedMissionInfo.builder()
                    .completedAt(missionLog.getCompletedAt())
                    .completedMissionUrl(missionLog.getCompletedMissionUrl())
                    .build()).collect(Collectors.toList());
        }
    }

    private String studyName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private MissionType missionType;
    private Integer missionCountPerWeek;
    private Integer uncompletedMissionCounts;
    private List<CompletedMissionInfo> completedMissionInfoList;

    public static Page<WeeklyMissionStateResponseDTO> toDTOPage(Page<MissionState> missionStates) {
        return missionStates.map(missionState -> WeeklyMissionStateResponseDTO.builder()
                .studyName(missionState.getStudyEnrollment().getStudy().getName())
                .startAt(missionState.getStartAt())
                .endAt(missionState.getEndAt())
                .missionType(missionState.getStudyEnrollment().getStudy().getMission().getMissionType())
                .missionCountPerWeek(missionState.getStudyEnrollment().getStudy().getMission().getMissionCountPerWeek())
                .uncompletedMissionCounts(missionState.getUncompletedMissionCounts())
                .completedMissionInfoList(CompletedMissionInfo.toDTO(missionState.getMissionLogs()))
                .build());
    }
}
