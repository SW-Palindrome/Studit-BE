package com.palindrome.studit.repository;

import com.palindrome.studit.domain.user.entity.OAuthInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class InMemoryUserRepository {
    private final HashMap<String, OAuthInfo> oAuthInfoMap = new HashMap<>();

    public void saveUser(OAuthInfo oAuthInfo) {
        oAuthInfoMap.put(oAuthInfo.getProviderId(), oAuthInfo);
    }
}
