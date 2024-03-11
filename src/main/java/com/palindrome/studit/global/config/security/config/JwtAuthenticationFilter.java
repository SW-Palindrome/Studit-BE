package com.palindrome.studit.global.config.security.config;

import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import com.palindrome.studit.global.config.security.application.TokenService;
import com.palindrome.studit.global.config.security.dto.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final TokenService tokenService;

    @Value("${jwt.type}")
    private String tokenType;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = extractToken(servletRequest);

        if (token != null && tokenService.validateToken(token)) {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String extractToken(ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader("Authorization");

        if (token != null && !token.isBlank() && token.startsWith(tokenType)) {
            return token.substring(tokenType.length() + 1);
        }

        return null;
    }

    public Authentication getAuthentication(String token) {
        try {
            Long subject = Long.parseLong(tokenService.parseSubject(token));
            JwtUserDetails user = JwtUserDetails.builder().userId(subject).build();
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (InvalidTokenException e) {
            throw new AccessDeniedException("잘못된 엑세스 토큰입니다.");
        }
    }
}
