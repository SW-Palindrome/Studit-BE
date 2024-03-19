package com.palindrome.studit.domain.study.task;

import org.springframework.scheduling.annotation.Scheduled;

public class GithubPolling {
    @Scheduled(cron = "0 */1 * ? * *")
    public void collectCommits() {
        System.out.println("hello");
    }
}
