package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyLogRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyLog;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import com.palindrome.studit.domain.study.exception.VelogPostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class VelogMissionService {

    private final StudyEnrollmentRepository studyEnrollmentRepository;
    private final StudyLogRepository studyLogRepository;
    private final String VELOG_RSS_FEED_URL = "https://v2.velog.io/rss";
    private final String VELOG_RSS_ITEM_PATTERN ="<link>(.+?)</link>.*?<pubDate>(.+?)</pubDate>";
    private final RestClient restClient = RestClient.create(VELOG_RSS_FEED_URL);

    @Value("${cron.velog.fetch-days}")
    private int FETCH_DAYS;

    @Scheduled(cron = "${cron.velog.fetch-interval}")
    public void fetchVelogPost() {

        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.VELOG, StudyStatus.IN_PROGRESS);

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String missionUrl = studyEnrollment.getMissionUrl();

            if (missionUrl == null || missionUrl.isBlank()) {
                continue;
            }

            String username = missionUrl.substring("https://velog.io/@".length());
            String rssFeed = restClient.get()
                    .uri("/{username}",username)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new VelogPostException();
                    })
                    .body(String.class);

            if (rssFeed == null) {
                continue;
            }

            Pattern pattern = Pattern.compile(VELOG_RSS_ITEM_PATTERN);
            Matcher matcher = pattern.matcher(rssFeed);

            while (matcher.find()) {
                String url = matcher.group(1);
                String date = matcher.group(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime completedAt = LocalDateTime.parse(date, formatter);

                if (completedAt.isBefore(LocalDateTime.now().minusDays(FETCH_DAYS))) {
                    continue;
                }

                StudyLog studyLog = StudyLog.builder()
                        .studyEnrollment(studyEnrollment)
                        .completedMissionUrl(url)
                        .completedAt(completedAt).build();
                studyLogRepository.save(studyLog);
            }
        }
    }
}
