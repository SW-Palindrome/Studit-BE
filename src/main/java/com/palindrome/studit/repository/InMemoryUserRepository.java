package com.palindrome.studit.repository;

import com.palindrome.studit.model.OAuthInfo;
import com.palindrome.studit.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class InMemoryUserRepository {
    private final HashMap<String, OAuthInfo> oAuthInfoMap = new HashMap<>();

    public void saveUser(OAuthInfo oAuthInfo) {
        System.out.println(oAuthInfoMap);
        if (oAuthInfoMap.get(oAuthInfo.getProviderId()) != null) {
            System.out.println("provider id duplicated!");
        }
        oAuthInfoMap.put(oAuthInfo.getProviderId(), oAuthInfo);
    }

    public User getUser(String providerId) {
        return oAuthInfoMap.get(providerId).getUser();
    }
}
