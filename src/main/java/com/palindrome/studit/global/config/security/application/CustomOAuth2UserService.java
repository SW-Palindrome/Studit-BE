package com.palindrome.studit.global.config.security.application;

import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.entity.OAuthProviderType;
import com.palindrome.studit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;
    private static final Map<String, OAuthProviderType> oAuthProviderTypeMap = Map.of(
        "github", OAuthProviderType.GITHUB
    );

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuthProviderType oAuthProviderType = oAuthProviderTypeMap.get(clientRegistration.getRegistrationId());
        User user = authService.createUser(oAuth2User.getAttribute("email"), oAuthProviderType, clientRegistration.getClientId());
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
