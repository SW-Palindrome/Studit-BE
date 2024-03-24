package com.palindrome.studit.domain.mission.application;

import com.palindrome.studit.domain.mission.dao.MissionStateRepository;
import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionStateRepository missionStateRepository;
    private static final int MISSION_DURATION_DATE = 7;

    @Transactional
    public List<MissionState> createMissionStates(StudyEnrollment studyEnrollment) {
        Study study = studyEnrollment.getStudy();

        List<MissionState> missionStates = new ArrayList<>();

        for (LocalDateTime dateTime = study.getStartAt(); dateTime.isBefore(study.getEndAt().minusDays(MISSION_DURATION_DATE)); dateTime = dateTime.plusDays(MISSION_DURATION_DATE)) {
            missionStates.add(MissionState.builder()
                    .studyEnrollment(studyEnrollment)
                    .startAt(dateTime)
                    .endAt(dateTime.plusDays(MISSION_DURATION_DATE))
                    .uncompletedMissionCounts(study.getMission().getMissionCountPerWeek())
                    .build());
        }

        return missionStateRepository.saveAll(missionStates);
    }
}
