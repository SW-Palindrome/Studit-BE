package com.palindrome.studit.domain.study.dto;

import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.mission.domain.MissionStatus;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyPurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class PrivateStudyDetailResponseDTO {
    private StudyDetail study;
    private List<Mission> missions;

    @Builder
    @Getter
    public static class StudyDetail {
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

        public static StudyDetail toDTO(Study study) {
            return StudyDetail.builder()
                    .studyId(study.getStudyId())
                    .name(study.getName())
                    .description(study.getDescription())
                    .purpose(study.getPurpose())
                    .missionType(study.getMission().getMissionType())
                    .missionCountPerWeek(study.getMission().getMissionCountPerWeek())
                    .finePerMission(study.getMission().getFinePerMission())
                    .studyImage(study.getStudyImage())
                    .startAt(study.getStartAt())
                    .endAt(study.getEndAt())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Mission {
        private String nickname;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private MissionStatus status;
        private String completedMissionUrl;

        public static List<Mission> toDTOList(List<MissionState> missionStates) {
            List<Mission> missions = new ArrayList<>();
            for (MissionState missionState : missionStates) {
                List<MissionLog> missionLogs = missionState.getMissionLogs();
                if (missionLogs != null && !missionLogs.isEmpty()) {
                    for (MissionLog missionLog : missionLogs) {
                        missions.add(Mission.builder()
                                .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                                .startAt(missionState.getStartAt())
                                .endAt(missionState.getEndAt())
                                .status(MissionStatus.COMPLETED)
                                .completedMissionUrl(missionLog.getCompletedMissionUrl())
                                .build());
                    }
                }
                Integer incompleteMissionCounts = missionState.getIncompleteMissionCounts();
                for (int i = 0; i < incompleteMissionCounts; i++) {
                    missions.add(Mission.builder()
                            .nickname(missionState.getStudyEnrollment().getUser().getNickname())
                            .startAt(missionState.getStartAt())
                            .endAt(missionState.getEndAt())
                            .status(MissionStatus.INCOMPLETE)
                            .completedMissionUrl(null)
                            .build());
                }
            }
            return missions;
        }
    }
    public static PrivateStudyDetailResponseDTO toDTO(Study study, List<MissionState> missionStates) {
        PrivateStudyDetailResponseDTO.StudyDetail studyDetail = PrivateStudyDetailResponseDTO.StudyDetail.toDTO(study);
        List<PrivateStudyDetailResponseDTO.Mission> missions = PrivateStudyDetailResponseDTO.Mission.toDTOList(missionStates);
        return PrivateStudyDetailResponseDTO.builder()
                .study(studyDetail)
                .missions(missions)
                .build();
    }
}
