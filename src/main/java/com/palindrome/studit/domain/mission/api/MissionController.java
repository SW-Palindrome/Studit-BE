package com.palindrome.studit.domain.mission.api;

import com.palindrome.studit.domain.mission.application.MissionService;
import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.mission.dto.ActivityDTO;
import com.palindrome.studit.domain.mission.dto.WeeklyMissionStateResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/missions")
public class MissionController {
    private final MissionService missionService;

    @GetMapping("/my/weekly")
    public Page<WeeklyMissionStateResponseDTO> listMyWeeklyMissionStates(Authentication authentication, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<MissionState> missionStates = missionService.listMyWeeklyMissionStates(page, Long.parseLong(authentication.getName()), LocalDateTime.now());
        return WeeklyMissionStateResponseDTO.toDTOPage(missionStates);
    }

    @GetMapping("/my/activities")
    public Page<ActivityDTO> listActivities(Authentication authentication, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<MissionLog> missionLogs = missionService.listAllActivities(page, Long.parseLong(authentication.getName()));
        return ActivityDTO.toDTOPage(missionLogs);
    }
}
