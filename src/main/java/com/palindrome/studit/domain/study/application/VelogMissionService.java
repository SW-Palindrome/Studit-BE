package com.palindrome.studit.domain.study.application;

import com.palindrome.studit.domain.study.dao.StudyEnrollmentRepository;
import com.palindrome.studit.domain.study.dao.StudyLogRepository;
import com.palindrome.studit.domain.study.domain.MissionType;
import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.domain.study.domain.StudyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VelogMissionService {

    private final RestTemplate restTemplate;
    private final StudyEnrollmentRepository studyEnrollmentRepository;
    private final StudyLogRepository studyLogRepository;
    private final String VELOG_RSS_FEED_URL = "https://v2/velog.io/rss/";
    private final String VELOG_RSS_ITEM_PATTERN ="<link>(.+?)</link>.*?<pubDate>(.+?)</pubDate>";

    @Scheduled(cron = "0 */10 * ? * *")
    public void saveNewPost() {
        List<StudyEnrollment> studyEnrollments = studyEnrollmentRepository.findAllByStudy_Mission_MissionTypeAndStudy_Status(MissionType.GITHUB, StudyStatus.IN_PROGRESS);

        for (StudyEnrollment studyEnrollment : studyEnrollments) {
            String missionUrl = studyEnrollment.getMissionUrl();

            if (missionUrl == null || missionUrl.isBlank()) {
                continue;
            }

            String username = missionUrl.substring("https://velog.io/@".length());
            String rssFeedUrl =  VELOG_RSS_FEED_URL + username;
            String rssFeed = restTemplate.getForObject(rssFeedUrl, String.class);

            if (rssFeed == null) {
                continue;
            }

            Pattern pattern = Pattern.compile(VELOG_RSS_ITEM_PATTERN);
            Matcher matcher = pattern.matcher(rssFeed);

            LocalDateTime latestCompletedAt =
        }
    }
}
