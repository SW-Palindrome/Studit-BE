package com.palindrome.studit.security;

import com.palindrome.studit.domain.user.application.UserService;
import com.palindrome.studit.domain.user.entity.OAuthInfo;
import com.palindrome.studit.domain.user.entity.OAuthProviderType;
import com.palindrome.studit.domain.user.entity.User;
import com.palindrome.studit.domain.user.entity.UserRoleType;
import com.palindrome.studit.repository.OAuthInfoRepository;
import com.palindrome.studit.repository.UserRepository;
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
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuthProviderType oAuthProviderType;
        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "github":
                oAuthProviderType = OAuthProviderType.GITHUB;
                break;
            default:
                throw new OAuth2AuthenticationException("client id not match");
        }
        User user = userService.create(oAuth2User.getAttribute("email"), oAuthProviderType, clientRegistration.getClientId());
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
