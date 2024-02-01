package com.palindrome.studit.security;

import com.palindrome.studit.enums.OAuthProviderType;
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
        createUser(userRequest.getClientRegistration());
        String email = oAuth2User.getAttribute("email");
        if (email != null) {
            System.out.println(email);
        }
        return oAuth2User;
    }

    private void createUser(ClientRegistration clientRegistration) {
        User user = new User();
        OAuthInfo oAuthInfo = new OAuthInfo();
        oAuthInfo.setUser(user);
        switch (clientRegistration.getRegistrationId()) {
            case "github":
                oAuthInfo.setOAuthProviderType(OAuthProviderType.GITHUB);
                break;
        }
        oAuthInfo.setProviderId(clientRegistration.getClientId());
        inMemoryUserRepository.saveUser(oAuthInfo);
    }
}
