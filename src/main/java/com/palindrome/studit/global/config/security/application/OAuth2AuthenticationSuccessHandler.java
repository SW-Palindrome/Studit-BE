package com.palindrome.studit.global.config.security.application;

import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

import static com.palindrome.studit.global.config.security.dao.OAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        String accessToken = tokenService.createAccessToken(oAuth2User.getName());
        String refreshToken = tokenService.createRefreshToken();

        User user = userRepository.findById(Long.parseLong(oAuth2User.getName())).orElse(null);
        if (user == null) {
            return;
        }
        authService.updateRefreshToken(user, refreshToken);

        Cookie cookie = Arrays.stream(request.getCookies()).filter(s -> s.getName().equals(REDIRECT_URI_PARAM_COOKIE_NAME)).findAny().orElse(null);

        if (cookie != null) {
            String targetUri = UriComponentsBuilder.fromUriString(cookie.getValue())
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, targetUri);
        }
    }
}
