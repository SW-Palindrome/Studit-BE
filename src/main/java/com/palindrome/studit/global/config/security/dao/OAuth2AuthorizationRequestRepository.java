package com.palindrome.studit.global.config.security.dao;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * OAuth2 사용자의 요청 정보 저장 관리
 * Default Request Repository인 HttpSessionOAuth2AuthorizationRequestRepository 참조
 * 세션을 통한 사용자 식별(HttpSessionOAuth2AuthorizationRequestRepository) + 쿠키를 통한 redirect uri
 */
@Component
public class OAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    private static final String DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME = HttpSessionOAuth2AuthorizationRequestRepository.class.getName() + ".AUTHORIZATION_REQUEST";
    private final String sessionAttributeName;

    public OAuth2AuthorizationRequestRepository() {
        this.sessionAttributeName = DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        } else {
            OAuth2AuthorizationRequest authorizationRequest = this.getAuthorizationRequest(request);
            return authorizationRequest != null && stateParameter.equals(authorizationRequest.getState()) ? authorizationRequest : null;
        }
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (authorizationRequest == null) {
            this.removeAuthorizationRequest(request, response);
        } else {
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
            request.getSession().setAttribute(this.sessionAttributeName, authorizationRequest);
        }
        saveRedirectUriInCookie(request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(response, "response cannot be null");
        OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            request.getSession().removeAttribute(this.sessionAttributeName);
        }

        return authorizationRequest;
    }

    private String getStateParameter(HttpServletRequest request) {
        return request.getParameter("state");
    }

    private OAuth2AuthorizationRequest getAuthorizationRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (OAuth2AuthorizationRequest)session.getAttribute(this.sessionAttributeName) : null;
    }

    public void saveRedirectUriInCookie(HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (redirectUri != null && !redirectUri.isBlank()) {
            Cookie cookie = new Cookie(REDIRECT_URI_PARAM_COOKIE_NAME, redirectUri);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
            response.addCookie(cookie);
        }
    }
}
