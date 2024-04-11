package com.palindrome.studit.domain.mission.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.palindrome.studit.domain.mission.dao.MissionLogRepository;
import com.palindrome.studit.domain.mission.domain.MissionLog;
import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import com.palindrome.studit.domain.mission.dto.VelogPostRequestDTO;
import com.palindrome.studit.domain.mission.variable.Variables;
import com.palindrome.studit.domain.mission.variable.VelogPostVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VelogMissionService {

    private final StudyEnrollmentRepository studyEnrollmentRepository;
    private final MissionLogRepository missionLogRepository;
    private final MissionService missionService;

    private final String VELOG_URL = "https://velog.io/@";
    private final String VELOG_GRAPHQL_URL = "https://v3.velog.io/graphql";
    private final RestTemplate restTemplate;

    @Value("${cron.velog.fetch-minutes}")
    private int FETCH_MINUTES;

    @Scheduled(cron = "${cron.velog.fetch-interval}")
    public void fetchVelogPost() {

        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.VELOG, StudyStatus.IN_PROGRESS);

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String missionUrl = studyEnrollment.getMissionUrl();
            String missionTag = studyEnrollment.getStudy().getTag();

            if (missionUrl == null || missionTag == null) {
                continue;
            }

            String username = missionUrl.substring(VELOG_URL.length());
            String query = """
                    query Posts($username: String!, $tag: String!) {
                        posts(input: { username: $username, tag: $tag }) {
                            title
                            released_at
                            url_slug
                        }
                    }
                    """;

            Variables variables = VelogPostVariables.builder()
                    .username(username)
                    .tag(missionTag)
                    .build();

            VelogPostRequestDTO velogPostRequestDTO = VelogPostRequestDTO.builder()
                    .queryFieldName("Posts")
                    .variables(variables)
                    .query(query)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/graphql");
            HttpEntity<VelogPostRequestDTO> requestEntity = new HttpEntity<>(velogPostRequestDTO);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(VELOG_GRAPHQL_URL, requestEntity, JsonNode.class);

            if (!response.hasBody()) {
                continue;
            }

            JsonNode responseData = response.getBody().get("data");

            if (!responseData.has("posts")) {
                continue;
            }

            JsonNode posts = responseData.get("posts");

            for (JsonNode post : posts) {
                String urlSlug = post.get("url_slug").asText();
                String completedMissionUrl = missionUrl + "/" + urlSlug;
                LocalDateTime completedAt = LocalDateTime.parse(post.get("released_at").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);

                if (completedAt.isBefore(LocalDateTime.now().minusMinutes(FETCH_MINUTES))) {
                    continue;
                }

                getOrCreateMissionLog(studyEnrollment, completedMissionUrl, completedAt);
            }
        }
    }

    private MissionLog getOrCreateMissionLog(StudyEnrollment studyEnrollment, String completedMissionUrl, LocalDateTime completedAt) {
        return missionLogRepository.findByCompletedMissionUrl(completedMissionUrl)
                .orElseGet(() -> missionService.submitMission(studyEnrollment, completedMissionUrl, completedAt));
    }
}
