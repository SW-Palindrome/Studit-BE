package com.palindrome.studit.domain.mission.dto;

import com.palindrome.studit.domain.mission.domain.MissionLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ActivityDTO {
    private String studyName;
    private String completedMissionUrl;
    private String userName;
    private LocalDateTime completedAt;

    public static Page<ActivityDTO> toDTOPage(Page<MissionLog> missionLogs) {
        return missionLogs.map(missionLog -> ActivityDTO.builder()
                .studyName(missionLog.getMissionState().getStudyEnrollment().getStudy().getName())
                .completedMissionUrl(missionLog.getCompletedMissionUrl())
                .userName(missionLog.getMissionState().getStudyEnrollment().getUser().getNickname())
                .completedAt(missionLog.getCompletedAt())
                .build());
    }
}
