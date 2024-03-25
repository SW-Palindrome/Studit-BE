package com.palindrome.studit.domain.study.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyLogRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyLog;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubMissionService {
    private final StudyEnrollmentRepository studyEnrollmentRepository;
    private final StudyLogRepository studyLogRepository;
    private final RestTemplate restTemplate;

    @Value("${cron.github.fetch-days}")
    private int FETCH_DAYS;

    @Scheduled(cron = "${cron.github.fetch-interval}")
    public void savePullRequest() {
        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.GITHUB, StudyStatus.IN_PROGRESS);

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String url = getGithubApiUrl(studyEnrollment.getMissionUrl());

            if (url == null) continue;

            JsonNode result = restTemplate.getForObject(url, JsonNode.class);

            if (result == null) continue;

            for (JsonNode repoInfo : result) {
                if (!repoInfo.get("state").asText().equals("closed")) continue;

                LocalDateTime completedAt = LocalDateTime.parse(repoInfo.get("merged_at").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
                getOrCreateStudyLog(studyEnrollment, repoInfo.get("html_url").asText(), completedAt);
            }
        }
    }

    private String getGithubApiUrl(String missionUrl) {
        if (missionUrl == null || missionUrl.isBlank()) return null;

        String[] parts = missionUrl.split("/");

        String user = parts[3];
        String repositoryName = parts[4];

        return "https://api.github.com/repos/" + user + "/" + repositoryName + "/pulls?state=all";
    }

    private StudyLog getOrCreateStudyLog(StudyEnrollment studyEnrollment, String completedMissionUrl, LocalDateTime completedAt) {
        if (completedAt.isBefore(LocalDateTime.now().minusDays(FETCH_DAYS))) return null;

        StudyLog studyLog = StudyLog.builder()
                .studyEnrollment(studyEnrollment)
                .completedMissionUrl(completedMissionUrl)
                .completedAt(completedAt)
                .build();

        return studyLogRepository.findByCompletedMissionUrl(completedMissionUrl).orElseGet(() -> studyLogRepository.save(studyLog));
    }
}
