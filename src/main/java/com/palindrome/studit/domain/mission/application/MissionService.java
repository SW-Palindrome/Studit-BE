package com.palindrome.studit.domain.mission.application;

import com.palindrome.studit.domain.mission.dao.MissionLogRepository;
import com.palindrome.studit.domain.mission.dao.MissionStateRepository;
import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.mission.domain.MissionState;
import com.palindrome.studit.domain.study.dao.StudyRepository;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionStateRepository missionStateRepository;
    private final MissionLogRepository missionLogRepository;
    private final StudyRepository studyRepository;
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

    @Transactional
    public MissionLog submitMission(StudyEnrollment studyEnrollment, String completedMissionUrl, LocalDateTime completedAt) {
        MissionState missionState = missionStateRepository.findByStudyEnrollment_StudyEnrollmentIdAndStartAtBeforeAndEndAtAfter(studyEnrollment.getStudyEnrollmentId(), completedAt, completedAt).orElseThrow();

        missionState.submitMission();

        MissionLog missionLog = MissionLog.builder()
                .missionState(missionState)
                .completedMissionUrl(completedMissionUrl)
                .completedAt(completedAt)
                .build();

        return missionLogRepository.save(missionLog);
    }

    @Transactional
    public Page<MissionState> listMyWeeklyMissionStates(Integer page, Long userId, LocalDateTime today) {
        Pageable pageable = PageRequest.of(page, 10, Sort.DEFAULT_DIRECTION, "missionStateId");
        return missionStateRepository.findAllByStudyEnrollment_User_UserIdAndStartAtBeforeAndEndAtAfter(pageable, userId, today, today);
    }


    @Transactional
    public Page<MissionLog> listAllActivities(Integer page, Long userId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "completedAt");
        List<Study> studies = studyRepository.findAllByEnrollments_User_UserId(userId);
        return missionLogRepository.findAllByMissionState_StudyEnrollment_StudyIn(pageable, studies);
    }
}
