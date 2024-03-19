package com.palindrome.studit.domain.study.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GithubMissionService {
    private final StudyEnrollmentRepository studyEnrollmentRepository;

    @Scheduled(cron = "0 */1 * ? * *")
    public void collectCommits() {
        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.GITHUB, StudyStatus.IN_PROGRESS);
        RestTemplate restTemplate = new RestTemplate();

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String missionUrl = studyEnrollment.getMissionUrl();

            if (missionUrl == null || missionUrl.isBlank()) {
                continue;
            }

            System.out.println(missionUrl);

            String[] parts = missionUrl.split("/");

            String user = parts[3];
            String repositoryName = parts[4];

            String url = "https://api.github.com/repos/" + user + "/" + repositoryName + "/pulls?state=all";
            JsonNode result = restTemplate.getForObject(url, JsonNode.class);

            if (result == null) {
                continue;
            }

            for (JsonNode repoInfo : result) {
                System.out.println(repoInfo.get("html_url"));
            }
        }
    }
}
