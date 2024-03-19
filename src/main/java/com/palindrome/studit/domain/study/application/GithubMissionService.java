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
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Scheduled(cron = "0 */10 * ? * *")
    public void savePullRequest() {
        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.GITHUB, StudyStatus.IN_PROGRESS);
        RestTemplate restTemplate = new RestTemplate();

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String missionUrl = studyEnrollment.getMissionUrl();

            if (missionUrl == null || missionUrl.isBlank()) {
                continue;
            }

            String[] parts = missionUrl.split("/");

            String user = parts[3];
            String repositoryName = parts[4];

            String url = "https://api.github.com/repos/" + user + "/" + repositoryName + "/pulls?state=all";
            JsonNode result = restTemplate.getForObject(url, JsonNode.class);

            if (result == null) {
                continue;
            }

            for (JsonNode repoInfo : result) {
                String completedMissionUrl = repoInfo.get("html_url").asText();
                LocalDateTime completedAt = LocalDateTime.parse(repoInfo.get("merged_at").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

                StudyLog studyLog = StudyLog.builder()
                        .studyEnrollment(studyEnrollment)
                        .completedMissionUrl(completedMissionUrl)
                        .completedAt(completedAt)
                        .build();

                try {
                    studyLogRepository.save(studyLog);
                } catch (ConstraintViolationException | DataIntegrityViolationException e) {
                    log.warn(completedMissionUrl + "is ignored!");
                }
            }
        }
    }
}
