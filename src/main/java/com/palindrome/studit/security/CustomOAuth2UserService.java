package com.palindrome.studit.security;

import com.palindrome.studit.domain.user.entity.OAuthProviderType;
import com.palindrome.studit.model.OAuthInfo;
import com.palindrome.studit.model.User;
import com.palindrome.studit.repository.InMemoryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final InMemoryUserRepository inMemoryUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        User user = createUser(userRequest.getClientRegistration());
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User createUser(ClientRegistration clientRegistration) {
        User user = new User();
        user.setNickName(clientRegistration.getClientId());
        OAuthInfo oAuthInfo = new OAuthInfo();
        oAuthInfo.setUser(user);
        switch (clientRegistration.getRegistrationId()) {
            case "github":
                oAuthInfo.setOAuthProviderType(OAuthProviderType.GITHUB);
                break;
        }
        oAuthInfo.setProviderId(clientRegistration.getClientId());
        inMemoryUserRepository.saveUser(oAuthInfo);
        return user;
    }
}
